package Express.model.po;

public class CampusPo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column campus.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column campus.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column campus.building
     *
     * @mbg.generated
     */
    private String building;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column campus.id
     *
     * @return the value of campus.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column campus.id
     *
     * @param id the value for campus.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column campus.name
     *
     * @return the value of campus.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column campus.name
     *
     * @param name the value for campus.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column campus.building
     *
     * @return the value of campus.building
     *
     * @mbg.generated
     */
    public String getBuilding() {
        return building;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column campus.building
     *
     * @param building the value for campus.building
     *
     * @mbg.generated
     */
    public void setBuilding(String building) {
        this.building = building == null ? null : building.trim();
    }
}