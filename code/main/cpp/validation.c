#include <jni.h>
#include <string.h>
#include <stdbool.h>
#include <regex.h>

bool validateUsername(const char *username) {
    if (strlen(username) < 4) {
        return false;
    }

    regex_t regex;
    int result;
    const char *pattern = "^[A-Za-zА-Яа-я0-9_]+$";

    if (regcomp(&regex, pattern, REG_EXTENDED)) {
        return false;
    }

    result = regexec(&regex, username, 0, NULL, 0);
    regfree(&regex);

    return result == 0;
}

bool validatePassword(const char *password) {
    if (strlen(password) < 5) {
        return false;
    }

    regex_t regex;
    int result;
    const char *pattern = "^[A-Za-zА-Яа-я0-9_]+$";

    if (regcomp(&regex, pattern, REG_EXTENDED)) {
        return false;
    }

    result = regexec(&regex, password, 0, NULL, 0);
    regfree(&regex);

    return result == 0;
}

JNIEXPORT jboolean JNICALL Java_com_example_movie_Validation_validateUsername(JNIEnv *env, jobject obj, jstring jUsername) {
    const char *username = (*env)->GetStringUTFChars(env, jUsername, NULL);
    if (username == NULL) {
        return JNI_FALSE;
    }

    int result = validateUsername(username);

    (*env)->ReleaseStringUTFChars(env, jUsername, username);

    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_example_movie_Validation_validatePassword(JNIEnv *env, jobject obj, jstring jPassword) {
    const char *password = (*env)->GetStringUTFChars(env, jPassword, NULL);
    if (password == NULL) {
        return JNI_FALSE;
    }

    int result = validatePassword(password);

    (*env)->ReleaseStringUTFChars(env, jPassword, password);

    return result ? JNI_TRUE : JNI_FALSE;
}