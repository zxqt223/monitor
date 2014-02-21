package com.zhangyue.monitor.alarm;

/**
 * @Descriptions The class AlarmMessage.java's implementation：this class defines
 *               all alram messages fields
 * @author scott 2013-8-19 上午10:11:48
 * @version 1.0
 */
public class AlarmMessage {

    private String[] mobilePhoneNumbers;
    private String[] emailToAddress;

    private String title = null;
    private String content = null;

    public AlarmMessage(String title, String content){
        super();
        this.title = title;
        this.content = content;
    }

    public AlarmMessage(String[] mobilePhoneNumbers, String[] emailToAddress,
                        String title, String content){
        super();
        this.mobilePhoneNumbers = mobilePhoneNumbers;
        this.emailToAddress = emailToAddress;
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String toString() {
        return title + ",[" + content + "]";
    }

    public String[] getMobilePhoneNumbers() {
        return mobilePhoneNumbers;
    }

    public void setMobilePhoneNumbers(String[] mobilePhoneNumbers) {
        this.mobilePhoneNumbers = mobilePhoneNumbers;
    }

    public String[] getEmailToAddress() {
        return emailToAddress;
    }

    public void setEmailToAddress(String[] emailToAddress) {
        this.emailToAddress = emailToAddress;
    }
}
