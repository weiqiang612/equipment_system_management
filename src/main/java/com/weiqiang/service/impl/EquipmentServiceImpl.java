package com.weiqiang.service.impl;

import com.weiqiang.dao.EquipmentDao;
import com.weiqiang.pojo.Equipment;
import com.weiqiang.pojo.EquipmentDepreciationVO;
import com.weiqiang.pojo.PageBean;
import com.weiqiang.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentDao equipmentDao;

    @Override
    public List<Equipment> getEquipments() {
        return equipmentDao.getEquipments();
    }

    @Override
    public Equipment getEquipmentById(String equipId) {
        return equipmentDao.getEquipmentById(equipId);
    }

    @Override
    public int addEquipment(Equipment equipment) {
        return equipmentDao.addEquipment(equipment);
    }

    @Override
    public int updateEquipment(Equipment equipment) {
        return equipmentDao.updateEquipment(equipment);
    }

    @Override
    public boolean deleteEquipment(String equipId) {
        return equipmentDao.deleteEquipment(equipId);
    }

    @Override
    public PageBean getEquipmentsDynamic(String equipName, String unitCode, String categoryId, String status, LocalDate begin, LocalDate end, Integer page, Integer pageSize) {

        // 获取总记录数量
        Long total = equipmentDao.getEquipmentsNum(equipName, unitCode, categoryId, status, begin, end);

        List<Equipment> equipmentsDynamic = equipmentDao.getEquipmentsDynamic(equipName, unitCode, categoryId, status, begin, end, page, pageSize);

        // 没有查询到结果 返回空数组 不返回 null
        if (equipmentsDynamic == null) {
            equipmentsDynamic = new ArrayList<>();
        }
        if (total == null) {
            total = 0L;
        }
        return new PageBean(total, equipmentsDynamic);
    }

    // 专门为导出表格写的全量查询 此时要连带将设备的折旧信息查询出来
    @Override
    public List<EquipmentDepreciationVO> getEquipmentsDynamicForExport(String equipName, String unitCode, String categoryId, String status, LocalDate begin, LocalDate end) {
        // 由于更改了动态查询的逻辑，将设备的 usefulLife 和 residualRate 一并查询出来了
        // 所以直接使用这些设备的信息计算折旧即可
        List<Equipment> list = equipmentDao.getEquipmentsDynamic(equipName, unitCode, categoryId, status, begin, end, null, null);
        // 将每台设备的折旧信息计算出来，然后映射到 List<EquipmentDepreciationVO> 返回
        return list.stream().map(this::calculateAccumulated).collect(Collectors.toList());
    }

    /**
     *
     * 计算某台设备的累积折旧
     * 使用最常见的平均年限法（直线折旧法）
     * @param equipment 要计算折旧的设备
     * @return 将相关数据封装成 EquipmentDepreciationVO 返回
     */
    public EquipmentDepreciationVO calculateAccumulated(Equipment equipment) {
        // 将连表查询获得的残值率、预计使用年份、设备原值封装成 EquipmentDepreciationVO 返回

        EquipmentDepreciationVO vo = new EquipmentDepreciationVO();
        vo.setEquipId(equipment.getEquipId());
        vo.setResidualRate(equipment.getResidualRate());
        vo.setUsefulLife(equipment.getUsefulLife());
        vo.setOriginalValue(equipment.getOriginalValue());
        // 将对象的相关信息设置好
        vo.setEquipName(equipment.getEquipName());
        vo.setCategoryName(equipment.getCategoryName());
        vo.setUnitName(equipment.getUnitName());
        vo.setPurchaseDate(equipment.getPurchaseDate());
        vo.setStatus(equipment.getStatus());
        // 财务惯例：从购入日期的下个月 1 号开始计提
        // 计算已使用月份
        LocalDate startDate = vo.getPurchaseDate().plusMonths(1).withDayOfMonth(1);
        LocalDate now = LocalDate.now();
        long monthsUsed = ChronoUnit.MONTHS.between(startDate, now);
        // 还没有到计提时间
        if (monthsUsed < 0) monthsUsed = 0;
        // 使用总月数 如果使用月数已经超过该值，则不再折旧
        int totalLifeMonths = vo.getUsefulLife() * 12;
        if (monthsUsed > totalLifeMonths) monthsUsed = totalLifeMonths;
        // 应提折旧总额 = 原值 * (1 - residualRate)  // 最多折旧额
        BigDecimal totalDepreciable = equipment.getOriginalValue().multiply(BigDecimal.ONE.subtract(vo.getResidualRate()));
        // 月折旧额 = 应提折旧总额 / (预计使用年份 * 12) 保留10位精度
        BigDecimal mouthlyDepreciation = totalDepreciable.divide(BigDecimal.valueOf(totalLifeMonths),10, RoundingMode.HALF_UP);
        // 累积折旧 = 使用月份 * 月折旧额
        BigDecimal accumulatedDepreciation = mouthlyDepreciation.multiply(BigDecimal.valueOf(monthsUsed)).setScale(2, RoundingMode.HALF_UP);;

        // 累积折旧和应提折旧总额相等时，设置 isFullyDepreciated 提足折旧标识
        if (monthsUsed >= totalLifeMonths) {
            vo.setIsFullyDepreciated(true);
            accumulatedDepreciation = totalDepreciable; // 确保不会超提
        } else {
            vo.setIsFullyDepreciated(false);
        }

        vo.setMonthlyDepreciation(mouthlyDepreciation.setScale(2,RoundingMode.HALF_UP));
        vo.setAccumulated(accumulatedDepreciation);
        // 当前净值 = 原值 - 累积折旧
        vo.setNetValue(vo.getOriginalValue().subtract(accumulatedDepreciation));
        return vo;
    }


}
