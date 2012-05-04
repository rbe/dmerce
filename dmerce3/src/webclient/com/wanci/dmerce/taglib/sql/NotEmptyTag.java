package com.wanci.dmerce.taglib.sql;

public class NotEmptyTag extends EmptyTag {

  /**
   * Constructor instructs the superclass to only
   * execute the body if the last ResultSet returned 
   * more than 0 rows.
   */
  public NotEmptyTag() {
    setValue(false);
  }

}

