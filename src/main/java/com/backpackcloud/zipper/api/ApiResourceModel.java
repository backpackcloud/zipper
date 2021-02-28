package com.backpackcloud.zipper.api;

import java.util.UUID;

public interface ApiResourceModel {

  UUID id();

  Class<? extends ApiResourceController> controllerClass();

}
