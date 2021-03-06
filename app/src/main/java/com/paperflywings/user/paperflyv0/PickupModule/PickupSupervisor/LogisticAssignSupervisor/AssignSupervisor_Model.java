package com.paperflywings.user.paperflyv0.PickupModule.PickupSupervisor.LogisticAssignSupervisor;

public class AssignSupervisor_Model  {
    private String m_names;
    private String address;
    private String m_address;
    private String assigned;
    private int totalcount;
    private String totalamount;
    int key_id;
    private int scan_count;
    private String phone_no;

    public String getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(String totalamount) {
        this.totalamount = totalamount;
    }

    public void setScan_count(int scan_count) {
        this.scan_count = scan_count;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public void setMerchant_code(String merchant_code) {
        this.merchant_code = merchant_code;
    }

    public void setPick_m_name(String pick_m_name) {
        this.pick_m_name = pick_m_name;
    }

    public String getPick_assign_status() {
        return pick_assign_status;
    }

    public void setPick_assign_status(String pick_assign_status) {
        this.pick_assign_status = pick_assign_status;
    }

    private String merchant_code;
    private String pick_m_name;
    private String pick_assign_status;


    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AssignSupervisor_Model(String m_names, String merchant_code, int totalcount, String phone_no, String pick_m_name, String m_address, String pick_assign_status, String address) {
        this.m_names = m_names;
        this.merchant_code = merchant_code;
        this.totalcount = totalcount;
        this.phone_no = phone_no;
        this.pick_m_name = pick_m_name;
        this.m_address = m_address;

        this.pick_assign_status = pick_assign_status;
        this.address = address;

    }

    public AssignSupervisor_Model(String m_names, String merchant_code,int totalcount,String phone_no,String pick_m_name,String m_address, String pick_assign_status) {
        this.m_names = m_names;
        this.merchant_code = merchant_code;
        this.totalcount = totalcount;
        this.phone_no = phone_no;
        this.pick_m_name = pick_m_name;
        this.m_address = m_address;
        this.pick_assign_status = pick_assign_status;

        }
    public AssignSupervisor_Model(String m_names, String m_address, String address) {
        this.m_names = m_names;
        this.m_address = m_address; // mainly the merchant code is saved here
        this.address = address; // main merchant address is saved here for creating a new order
        }

    public String getMerchant_code() {
        return merchant_code;
    }

    public String getPick_m_name() {
        return pick_m_name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public int getScan_count() {
        return scan_count;
    }

    public AssignSupervisor_Model(String totalamount)
        {
            this.totalamount = totalamount;
        }

    public AssignSupervisor_Model() {

    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public int getKey_id() {
        return key_id;
    }

    public void setKey_id(int key_id) {
        this.key_id = key_id;
    }

    public String getAssigned() {
        return assigned;
    }

    public String getM_names() {
        return m_names;
    }

    public String getM_address() {
        return m_address;
    }


    public void setM_names(String m_names) {
        this.m_names = m_names;
    }

    public void setM_address(String m_address) {
        this.m_address = m_address;
    }


}
