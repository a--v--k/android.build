/*
 * Copyright (C) 2013 The Common CLI viewer interface Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <stdint.h>

#include <javahelpers.h>

#ifdef __cplusplus
#else
int CharacterHelper_init(CharacterHelper* that, JNIEnv* env)
{
    that->jenv = env;
    that->cls = (*(that->jenv))->FindClass(that->jenv, "java/lang/Character");
    if (that->cls)
    {
        that->midToLowerCase = (*(that->jenv))->GetStaticMethodID(that->jenv, that->cls, "toLowerCase", "(C)C");
    }
    that->valid = that->cls && that->midToLowerCase;
    return that->valid;
}

unsigned short CharacterHelper_toLowerCase(CharacterHelper* that, unsigned short ch)
{
    return that->valid ? (*(that->jenv))->CallStaticCharMethod(that->jenv, that->cls, that->midToLowerCase, ch) : ch;
}

int ArrayListHelper_init(ArrayListHelper* that, JNIEnv* env)
{
    that->jenv = env;
    that->cls = (*(that->jenv))->FindClass(that->jenv, "java/util/ArrayList");
    if (that->cls)
    {
        that->cid = (*(that->jenv))->GetMethodID(that->jenv, that->cls, "<init>", "()V");
        that->midAdd = (*(that->jenv))->GetMethodID(that->jenv, that->cls, "add", "(Ljava/lang/Object;)Z");
    }
    that->valid = that->cls && that->cid && that->midAdd;
    return that->valid;
}

jobject ArrayListHelper_create(ArrayListHelper* that)
{
    return that->valid ? (*(that->jenv))->NewObject(that->jenv, that->cls, that->cid) : NULL;
}

void ArrayListHelper_add(ArrayListHelper* that, jobject arrayList, jobject obj)
{
    if (that->valid && arrayList)
    {
        (*(that->jenv))->CallBooleanMethod(that->jenv, arrayList, that->midAdd, obj);
    }
}

int RectFHelper_init(RectFHelper* that, JNIEnv* env)
{
    that->jenv = env;
    that->cls = (*(that->jenv))->FindClass(that->jenv, "android/graphics/RectF");
    if (that->cls)
    {
        that->cid = (*(that->jenv))->GetMethodID(that->jenv, that->cls, "<init>", "()V");
        that->fidLeft = (*(that->jenv))->GetFieldID(that->jenv, that->cls, "left", "F");
        that->fidTop = (*(that->jenv))->GetFieldID(that->jenv, that->cls, "top", "F");
        that->fidRight = (*(that->jenv))->GetFieldID(that->jenv, that->cls, "right", "F");
        that->fidBottom = (*(that->jenv))->GetFieldID(that->jenv, that->cls, "bottom", "F");
    }

    that->valid = that->cls && that->cid && that->fidLeft && that->fidTop && that->fidRight && that->fidBottom;
    return that->valid;
}

jobject RectFHelper_create(RectFHelper* that)
{
    return that->valid ? (*(that->jenv))->NewObject(that->jenv, that->cls, that->cid) : NULL;
}

jobject RectFHelper_setRectF(RectFHelper* that, jobject rectf, const float* coords)
{
    if (that->valid && rectf)
    {
        (*(that->jenv))->SetFloatField(that->jenv, rectf, that->fidLeft, (jfloat) coords[0]);
        (*(that->jenv))->SetFloatField(that->jenv, rectf, that->fidTop, (jfloat) coords[1]);
        (*(that->jenv))->SetFloatField(that->jenv, rectf, that->fidRight, (jfloat) coords[2]);
        (*(that->jenv))->SetFloatField(that->jenv, rectf, that->fidBottom, (jfloat) coords[3]);
    }
    return rectf;
}
#endif
