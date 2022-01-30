LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := standalone-jni

LOCAL_CFLAGS    := $(APP_CFLAGS)
LOCAL_CPPFLAGS  := $(APP_CPPFLAGS)
LOCAL_ARM_MODE  := $(APP_ARM_MODE)

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include

LOCAL_SRC_FILES := src/javahelpers.c

LOCAL_STATIC_LIBRARIES := standalone-log

include $(BUILD_STATIC_LIBRARY)

$(call import-module, prefab/standalone-log)