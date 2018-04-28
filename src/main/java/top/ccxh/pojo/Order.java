package top.ccxh.pojo;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    private String name;
    private Integer pire;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPire() {
        return pire;
    }

    public void setPire(Integer pire) {
        this.pire = pire;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Order{" +
                "name='" + name + '\'' +
                ", pire=" + pire +
                ", date=" + date +
                '}';
    }
}
