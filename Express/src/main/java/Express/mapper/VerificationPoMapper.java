package Express.mapper;

import Express.model.po.VerificationPo;
import Express.model.po.VerificationPoExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface VerificationPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    int insert(VerificationPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    int insertSelective(VerificationPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    List<VerificationPo> selectByExample(VerificationPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    VerificationPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") VerificationPo record, @Param("example") VerificationPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") VerificationPo record, @Param("example") VerificationPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(VerificationPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table verification
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(VerificationPo record);
}