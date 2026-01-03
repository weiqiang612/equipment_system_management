package com.weiqiang.dao;

import com.weiqiang.pojo.Equipment;
import com.weiqiang.pojo.EquipmentDepreciationVO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

/**
 * @author 袁志刚
 * @version 1.0
 */

@Repository
public class EquipmentDao extends BasicDao<Equipment> {


    public List<Equipment> getEquipments() {
        String sql = "SELECT equip_id equipId, equip_name equipName, model, status, " +
                "purchase_date purchaseDate, original_value originalValue, d.unit_code unitCode ,unit_name unitName " +
                ", c.category_id categoryId ,category_name categoryName " +
                "FROM equipment e " +
                "JOIN department d " +
                "ON e.unit_code = d.unit_code " +
                "JOIN category c " +
                "ON e.category_id = c.category_id";
        return mutiSelect(sql, Equipment.class, null);
    }

    public Equipment getEquipmentById(String equipId) {
        String sql = "SELECT equip_id equipId, equip_name equipName, model, status, " +
                "purchase_date purchaseDate, original_value originalValue, d.unit_code unitCode ,unit_name unitName " +
                ", c.category_id categoryId ,category_name categoryName " +
                "FROM equipment e " +
                "JOIN department d " +
                "ON e.unit_code = d.unit_code " +
                "JOIN category c " +
                "ON e.category_id = c.category_id " +
                "where equip_id = ?";
        return selectOne(sql, Equipment.class, equipId);
    }

    public int addEquipment(Equipment equipment) {
        String sql = "INSERT INTO equipment (equip_id, equip_name, model, status, purchase_date, original_value, unit_code, category_id) VALUES" +
                "(? , ? , ? , ? , ? , ? , ? , ?)";
        return update(sql, equipment.getEquipId(), equipment.getEquipName(), equipment.getModel(),
                equipment.getStatus(), equipment.getPurchaseDate(), equipment.getOriginalValue(),
                equipment.getUnitCode(), equipment.getCategoryId());
    }

    public int updateEquipment(Equipment equipment) {
        String sql = "UPDATE equipment" +
                " SET equip_name = ?, model = ?, status = ?, purchase_date = ?," +
                " original_value = ?, unit_code = ?, category_id = ?" +
                " WHERE equip_id = ?";
        return update(sql, equipment.getEquipName(), equipment.getModel(), equipment.getStatus(),
                equipment.getPurchaseDate(), equipment.getOriginalValue(), equipment.getUnitCode(),
                equipment.getCategoryId(), equipment.getEquipId());
    }

    /*
    DELETE FROM maintenance_record WHERE equip_id = 'E2023002';
    DELETE FROM scrap_record WHERE equip_id = 'E2023002';
    DELETE FROM transfer_record WHERE equip_id = 'E2023002';
    DELETE FROM equipment WHERE equip_id = 'E2023002';
    */

    // 删除设备，需要将检修表 调拨表 报废表的记录一并删除
    public boolean deleteEquipment(String equipId) {
        LinkedHashMap<String, List<Object>> sqls = new LinkedHashMap<>();
        // 四条SQL语句共用同一个参数
        List<Object> params = new ArrayList<>(Collections.singletonList(equipId));
        // 先删除从表，后删除主表
        String[] sqlList = {
                "DELETE FROM maintenance_record WHERE equip_id = ?",
                "DELETE FROM scrap_record WHERE equip_id = ?",
                "DELETE FROM transfer_record WHERE equip_id = ?",
                "DELETE FROM equipment WHERE equip_id = ?"
        };
        for (String s : sqlList) {
            sqls.put(s, params);
        }
        return updateWithTransaction(sqls);
    }

    // 根据所给条件进行动态查询
    public List<Equipment> getEquipmentsDynamic(String equipName, String unitCode, String categoryId, String status, LocalDate begin, LocalDate end,Integer page,Integer pageSize) {
        // 初始sql
        StringBuilder sql = new StringBuilder("SELECT equip_id equipId, equip_name equipName, model, status, " +
                "purchase_date purchaseDate, original_value originalValue, d.unit_code unitCode ,unit_name unitName " +
                ", c.category_id categoryId ,category_name categoryName ,c.useful_life usefulLife, c.residual_rate residualRate " +
                "FROM equipment e " +
                "JOIN department d " +
                "ON e.unit_code = d.unit_code " +
                "JOIN category c " +
                "ON e.category_id = c.category_id ");
        // 添加无用条件，简化了第一个参数的处理，后续拼接SQL语句直接加AND即可
        sql.append("where 1=1 ");
        // 参数
        ArrayList<Object> params = new ArrayList<>();
        // 判断参数是否为空
        // 支持编号和设备名称模糊查询
        if (equipName != null && !equipName.trim().isEmpty()) {
            sql.append("AND (equip_id LIKE ? OR e.equip_name like ?) ");
            // 就算参数没有填到正确的位置，但是因为是 OR 连接，所以没有影响
            params.add("%" + equipName + "%");
            params.add("%" + equipName + "%");
        }
        // 这里前端下拉选框 需要处理
        if (unitCode != null && !unitCode.trim().isEmpty()){
            sql.append("AND e.unit_code = ? ");
            params.add(unitCode);
        }
        if (categoryId != null && !categoryId.trim().isEmpty()){
            sql.append("AND e.category_id = ? ");
            params.add(categoryId);
        }
        if (status != null && !status.trim().isEmpty()){
            sql.append("AND status = ? ");
            params.add(status);
        }
        // 处理日期区间
        if (begin != null && end != null) {
            sql.append("AND purchase_date BETWEEN ? AND ? ");
            params.add(begin);
            params.add(end);
        }
        // 按购买日期降序排列 留空格，处理分页逻辑
        sql.append("order by purchase_date desc ");
        // 处理分页逻辑
        if (page != null && pageSize != null) {
            int offset = (page - 1) * pageSize;
            sql.append("limit ?,? ");
            params.add(offset);
            params.add(pageSize);
        }
        return mutiSelect(String.valueOf(sql), Equipment.class, params.toArray());
    }

    // 查询总数量
    public Long getEquipmentsNum(String equipName, String unitCode, String categoryId, String status, LocalDate begin, LocalDate end) {
        StringBuilder sql = new StringBuilder("SELECT count(*) " +
                " FROM equipment e " +
                " JOIN department d " +
                " ON e.unit_code = d.unit_code " +
                " JOIN category c " +
                " ON e.category_id = c.category_id  ");
        // 添加无用条件，简化了第一个参数的处理，后续拼接SQL语句直接加AND即可
        sql.append("where 1=1 ");
        // 参数
        ArrayList<Object> params = new ArrayList<>();
        // 判断参数是否为空
        // 支持编号和设备名称模糊查询
        if (equipName != null && !equipName.trim().isEmpty()) {
            sql.append("AND (equip_id LIKE ? OR e.equip_name like ?) ");
            // 就算参数没有填到正确的位置，但是因为是 OR 连接，所以没有影响
            params.add("%" + equipName + "%");
            params.add("%" + equipName + "%");
        }
        // 这里前端下拉选框 需要处理
        if (unitCode != null && !unitCode.trim().isEmpty()){
            sql.append("AND e.unit_code = ? ");
            params.add(unitCode);
        }
        if (categoryId != null && !categoryId.trim().isEmpty()){
            sql.append("AND e.category_id = ? ");
            params.add(categoryId);
        }
        if (status != null && !status.trim().isEmpty()){
            sql.append("AND status = ? ");
            params.add(status);
        }
        // 处理日期区间
        if (begin != null && end != null) {
            sql.append("AND purchase_date BETWEEN ? AND ? ");
            params.add(begin);
            params.add(end);
        }
        return (Long) singleSelect(String.valueOf(sql),params.toArray());
    }

//    public EquipmentDepreciationVO getCalParams(String equipId) {
//        // 连表查询某台设备的 原值 残值率 预计使用年限
//        String sql = "SELECT equip_id equipId,original_value originalValue,c.useful_life usefulLife,c.residual_rate residualRate " +
//                "FROM equipment e " +
//                "JOIN category c " +
//                "ON e.category_id = c.category_id  " +
//                "WHERE equip_id = ?";
//        return selectOne(sql,EquipmentDepreciationVO.class,equipId);
//    }
}
