package Express.model.po;

import java.time.LocalDateTime;

public class DemandPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.type
     *
     * @mbg.generated
     */
    private Byte type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.code
     *
     * @mbg.generated
     */
    private String code;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.mobile
     *
     * @mbg.generated
     */
    private String mobile;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.address
     *
     * @mbg.generated
     */
    private String address;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.destination
     *
     * @mbg.generated
     */
    private String destination;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.expect_time
     *
     * @mbg.generated
     */
    private String expectTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.status
     *
     * @mbg.generated
     */
    private Byte status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.sponsor_id
     *
     * @mbg.generated
     */
    private Long sponsorId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.price
     *
     * @mbg.generated
     */
    private Integer price;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.comment
     *
     * @mbg.generated
     */
    private String comment;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.deleted
     *
     * @mbg.generated
     */
    private Byte deleted;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.gmt_create
     *
     * @mbg.generated
     */
    private LocalDateTime gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column demand.gmt_modified
     *
     * @mbg.generated
     */
    private LocalDateTime gmtModified;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.id
     *
     * @return the value of demand.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.id
     *
     * @param id the value for demand.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.type
     *
     * @return the value of demand.type
     *
     * @mbg.generated
     */
    public Byte getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.type
     *
     * @param type the value for demand.type
     *
     * @mbg.generated
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.code
     *
     * @return the value of demand.code
     *
     * @mbg.generated
     */
    public String getCode() {
        return code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.code
     *
     * @param code the value for demand.code
     *
     * @mbg.generated
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.mobile
     *
     * @return the value of demand.mobile
     *
     * @mbg.generated
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.mobile
     *
     * @param mobile the value for demand.mobile
     *
     * @mbg.generated
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.address
     *
     * @return the value of demand.address
     *
     * @mbg.generated
     */
    public String getAddress() {
        return address;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.address
     *
     * @param address the value for demand.address
     *
     * @mbg.generated
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.destination
     *
     * @return the value of demand.destination
     *
     * @mbg.generated
     */
    public String getDestination() {
        return destination;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.destination
     *
     * @param destination the value for demand.destination
     *
     * @mbg.generated
     */
    public void setDestination(String destination) {
        this.destination = destination == null ? null : destination.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.expect_time
     *
     * @return the value of demand.expect_time
     *
     * @mbg.generated
     */
    public String getExpectTime() {
        return expectTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.expect_time
     *
     * @param expectTime the value for demand.expect_time
     *
     * @mbg.generated
     */
    public void setExpectTime(String expectTime) {
        this.expectTime = expectTime == null ? null : expectTime.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.status
     *
     * @return the value of demand.status
     *
     * @mbg.generated
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.status
     *
     * @param status the value for demand.status
     *
     * @mbg.generated
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.sponsor_id
     *
     * @return the value of demand.sponsor_id
     *
     * @mbg.generated
     */
    public Long getSponsorId() {
        return sponsorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.sponsor_id
     *
     * @param sponsorId the value for demand.sponsor_id
     *
     * @mbg.generated
     */
    public void setSponsorId(Long sponsorId) {
        this.sponsorId = sponsorId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.price
     *
     * @return the value of demand.price
     *
     * @mbg.generated
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.price
     *
     * @param price the value for demand.price
     *
     * @mbg.generated
     */
    public void setPrice(Integer price) {
        this.price = price;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.comment
     *
     * @return the value of demand.comment
     *
     * @mbg.generated
     */
    public String getComment() {
        return comment;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.comment
     *
     * @param comment the value for demand.comment
     *
     * @mbg.generated
     */
    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.deleted
     *
     * @return the value of demand.deleted
     *
     * @mbg.generated
     */
    public Byte getDeleted() {
        return deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.deleted
     *
     * @param deleted the value for demand.deleted
     *
     * @mbg.generated
     */
    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.gmt_create
     *
     * @return the value of demand.gmt_create
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.gmt_create
     *
     * @param gmtCreate the value for demand.gmt_create
     *
     * @mbg.generated
     */
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column demand.gmt_modified
     *
     * @return the value of demand.gmt_modified
     *
     * @mbg.generated
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column demand.gmt_modified
     *
     * @param gmtModified the value for demand.gmt_modified
     *
     * @mbg.generated
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}