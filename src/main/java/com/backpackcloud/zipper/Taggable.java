package com.backpackcloud.zipper;

import java.util.Set;

public interface Taggable {

  Set<String> tags();

  void tag(String tagName);

}
