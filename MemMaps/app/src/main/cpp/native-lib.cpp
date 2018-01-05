#include <jni.h>
#include <string>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

extern char * readMemmap();
extern jstring strToJstring(JNIEnv* env, const char* pStr);

#define MAX_LENGTH 1024*1024
extern "C"
JNIEXPORT jstring

JNICALL
Java_com_memmaps_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    char * ret;
    std::string hello = "Hello from C++";
    ret = readMemmap();
    return strToJstring(env, ret);
}

char * readMemmap(){
    char target[255];
    char * output;
    char line[255];
    size_t         size = 0;

    memset(target, 0, 255);
    sprintf(target, "/proc/%d/maps", getpid());

    output = new char[MAX_LENGTH];
    int readed = 0;
    FILE* mapF = fopen(target, "r");

    while (fgets(line, 255, mapF) != NULL) {
        if(readed + strlen(line) > MAX_LENGTH)
        {
            return NULL;
        }
        memcpy(output + readed, line, strlen(line));
        readed += strlen(line);
    }
    fclose(mapF);
    return output;
}

jstring strToJstring(JNIEnv* env, const char* pStr)
{
    int        strLen    = strlen(pStr);
    jclass     jstrObj   = (env)->FindClass("java/lang/String");
    jmethodID  methodId  = (env)->GetMethodID(jstrObj, "<init>", "([BLjava/lang/String;)V");
    jbyteArray byteArray = (env)->NewByteArray(strLen);
    jstring    encode    = (env)->NewStringUTF("utf-8");
    (env)->SetByteArrayRegion(byteArray, 0, strLen, (jbyte*)pStr);

    return (jstring)(env)->NewObject(jstrObj, methodId, byteArray, encode);
}
