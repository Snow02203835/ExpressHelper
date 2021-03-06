package Express.mapper;

import Express.model.po.CampusPo;
import Express.model.po.CampusPoExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface CampusPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int insert(CampusPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int insertSelective(CampusPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    List<CampusPo> selectByExampleWithBLOBs(CampusPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    List<CampusPo> selectByExample(CampusPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    CampusPo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") CampusPo record, @Param("example") CampusPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int updateByExampleWithBLOBs(@Param("record") CampusPo record, @Param("example") CampusPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") CampusPo record, @Param("example") CampusPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(CampusPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int updateByPrimaryKeyWithBLOBs(CampusPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table campus
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(CampusPo record);
}