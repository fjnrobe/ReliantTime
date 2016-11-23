package dtos;


import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * This Dto holds the information necessary to build 1 row on the day entry grid.
 * Each row consists of the sir id, the nickname, and then a array of
 * 'eachLogEntry's, where each entry is used to determine the layout/spacing
 * of each entry: the space before the log entry, the width, and the label
 *
 * sample
 *
 */
public class DayEntryMatrixRowuiDto {
    private ObjectId id;
    private String sirNickname;
    private String linkParms;


    private ArrayList<ObjectId> logId;
    private ArrayList<Integer> spaceBeforeEntry;
    private ArrayList<Integer> entryWidth;
    private ArrayList<String> entryLabel;

    public ArrayList<Integer> getSpaceBeforeEntry() {
        return spaceBeforeEntry;
    }

    public void setSpaceBeforeEntry(ArrayList<Integer> spaceBeforeEntry) {
        this.spaceBeforeEntry = spaceBeforeEntry;
    }

    public ArrayList<Integer> getEntryWidth() {
        return entryWidth;
    }

    public void setEntryWidth(ArrayList<Integer> entryWidth) {
        this.entryWidth = entryWidth;
    }

    public ArrayList<String> getEntryLabel() {
        return entryLabel;
    }

    public void setEntryLabel(ArrayList<String> entryLabel) {
        this.entryLabel = entryLabel;
    }

    public String getLinkParms() {
        return linkParms;
    }

    public void setLinkParms(String linkParms) {
        this.linkParms = linkParms;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSirNickname() {
        return sirNickname;
    }

    public void setSirNickname(String sirNickname) {
        this.sirNickname = sirNickname;
    }

    public DayEntryMatrixRowuiDto()
    {
        spaceBeforeEntry = new ArrayList<Integer>();
        entryWidth = new ArrayList<Integer>();
        entryLabel = new ArrayList<String>();
        logId = new ArrayList<ObjectId>();
    }


    public ArrayList<ObjectId> getLogId() {
        return logId;
    }

    public void setLogId(ArrayList<ObjectId> logId) {
        this.logId = logId;
    }

}
