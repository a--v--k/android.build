LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := native-lib

LOCAL_CFLAGS    := $(APP_CFLAGS)
LOCAL_CPPFLAGS  := $(APP_CPPFLAGS) -DRARDLL
LOCAL_ARM_MODE := $(APP_ARM_MODE)

LOCAL_SRC_FILES := \
	NativeLibrary.cpp

LOCAL_STATIC_LIBRARIES :=  standalone-log standalone-jni

# uses Android log and z library (Android-3 Native API)
LOCAL_LDLIBS := -llog -lz

include $(BUILD_SHARED_LIBRARY)

# Import all modules that are included in the curl AAR.
$(call import-module, prefab/standalone-log)
$(call import-module, prefab/standalone-jni)
