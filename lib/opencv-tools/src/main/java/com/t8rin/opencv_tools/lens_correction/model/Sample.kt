/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("UnusedReceiverParameter")

package com.t8rin.opencv_tools.lens_correction.model

import com.t8rin.opencv_tools.lens_correction.LensCorrection

val LensCorrection.SAMPLE_LENS_PROFILE
    get() = "{\"name\":\"Canon_200d Mark II_EFS 17-55mm_Lens And Body Stabilization on_1080p_16by9_1920x1080-25.00fps\",\"note\":\"Lens And Body Stabilization on\",\"calibrated_by\":\"Kane McCarthy\",\"camera_brand\":\"Canon\",\"camera_model\":\"200d Mark II\",\"lens_model\":\"EFS 17-55mm\",\"camera_setting\":\"\",\"calib_dimension\":{\"w\":1920,\"h\":1080},\"orig_dimension\":{\"w\":1920,\"h\":1080},\"output_dimension\":{\"w\":1920,\"h\":1080},\"frame_readout_time\":null,\"gyro_lpf\":null,\"input_horizontal_stretch\":1.0,\"input_vertical_stretch\":1.0,\"num_images\":15,\"fps\":25.0,\"crop\":null,\"official\":false,\"asymmetrical\":false,\"fisheye_params\":{\"RMS_error\":0.6572613277627248,\"camera_matrix\":[[1672.4188601991846,0.0,933.6098162432148],[0.0,1677.6413802774082,539.4748019935479],[0.0,0.0,1.0]],\"distortion_coeffs\":[0.25149531135525693,-0.4430334263157348,3.9251216996331664,-8.164669122998902],\"radial_distortion_limit\":null},\"identifier\":\"\",\"calibrator_version\":\"1.5.4\",\"date\":\"2024-01-05\",\"compatible_settings\":[],\"sync_settings\":null,\"distortion_model\":null,\"digital_lens\":null,\"digital_lens_params\":null,\"interpolations\":null,\"focal_length\":null,\"crop_factor\":null,\"global_shutter\":false}"
