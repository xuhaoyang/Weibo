// IMessageServiceRemoteBinder.aidl
package com.xhy.weibo;
// Declare any non-default types here with import statements
import com.xhy.weibo.IMessageListener;
interface IMessageServiceRemoteBinder {

    void setAccount(String account);

    void setIsNotify(boolean flag);

    void setMessageListener (IMessageListener listener);
}
