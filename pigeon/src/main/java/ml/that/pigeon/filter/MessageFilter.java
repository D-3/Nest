package ml.that.pigeon.filter;

import ml.that.pigeon.msg.Message;

public interface MessageFilter {

  /**
   * Tests whether or not the specified message should pass the filter.
   *
   * @param msg the message to test
   * @return true if and only if <tt>msg</tt> passes the filter
   */
  boolean accept(Message msg);

}
