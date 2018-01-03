/*
 * Copyright (C) 2011 The Android Open Source Project
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

#pragma version(1)
#pragma rs java_package_name(com.example.android.rs.hellocompute)

rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
int rand;

const static float3 gMonoMult = {0.299f, 0.587f, 0.114f};

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    float4 f4 = rsUnpackColor8888(*v_in);
    float r = f4.r;
	float g = f4.g;
	float b = f4.b;
    
    if(rand == 0){
    	r = r / 3;
    	g = g / 3;
    	b = b * 5;
    	b = fmin(b, 1);
    }else if(rand == 1){
    	r = r * 5;
    	g = g / 3;
    	b = b / 3;
    	r = fmin (r, 1);
    }else if(rand == 2){
    	r = r / 3;
    	g = g * 5;
    	b = b / 3;
    	g = fmin (g, 1);
    }	
	
    float3 mono = {r, g, b};
    *v_out = rsPackColorTo8888(mono);
}

void filter() {
    rsForEach(gScript, gIn, gOut);
    
}

