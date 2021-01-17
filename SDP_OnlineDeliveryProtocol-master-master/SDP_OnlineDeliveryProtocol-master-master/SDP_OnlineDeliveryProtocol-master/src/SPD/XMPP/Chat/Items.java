package SPD.XMPP.Chat;

import MessageTemplate.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Items implements Serializable, Cloneable {
    private String ITEM_NAME;
    public Integer COUNT;
    ArrayList<String> FEEDBACK = new ArrayList<String>();


    public ArrayList<String> getFEEDBACK() {
        return FEEDBACK;
    }

    public void setFEEDBACK(ArrayList<String> FEEDBACK) {
        this.FEEDBACK = FEEDBACK;
    }

    public Items() {
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Items items = (Items) o;
        return Objects.equals(ITEM_NAME, items.ITEM_NAME);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ITEM_NAME, COUNT);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Items clone = (Items) super.clone();
        return clone;
    }

    public String getITEM_NAME() {
        return ITEM_NAME;
    }

    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }

    public Integer getCOUNT() {
        return COUNT;
    }

    public void setCOUNT(Integer COUNT) {
        this.COUNT = COUNT;
    }

    @Override
    public String toString() {
        return  "ITEM_NAME='" + ITEM_NAME + '\'' +
                ", COUNT=" + COUNT ;

    }

    public Items(String ITEM_NAME, Integer COUNT) {
        this.ITEM_NAME = ITEM_NAME;
        this.COUNT = COUNT;
    }
}

