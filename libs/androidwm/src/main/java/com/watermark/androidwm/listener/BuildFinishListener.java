/*
 *    Copyright 2018 Yizheng Huang
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.watermark.androidwm.listener;


/**
 * This interface is for listening if the task of
 * creating invisible watermark is finished.
 *
 * @param <T> can be the image and the string watermarks.
 * @author huangyz0918 (huangyz0918@gmail.com)
 */
public interface BuildFinishListener<T> {

    void onSuccess(T object);

    void onFailure(String message);
}
