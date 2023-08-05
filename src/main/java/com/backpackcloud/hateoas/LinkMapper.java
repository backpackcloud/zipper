package com.backpackcloud.hateoas;

public interface LinkMapper<R> {

  LinkMapper<R> title(String title);

  R to(String rel);

}
