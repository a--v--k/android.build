#include <cstdlib>
#include <jni.h>

#include "StLog.h"
#include "StEnv.h"

#define LCTX "NativeLibrary"
#define DEBUG_ENABLED false

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK)
    {
        return -1;
    }

    JByteArray arr(env, nullptr);

    DEBUG_L(DEBUG_ENABLED, LCTX, "Load JNI library: %d", arr.isValid());
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved)
{
    DEBUG_L(DEBUG_ENABLED, LCTX, "Unload JNI library");
}


