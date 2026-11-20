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

package com.t8rin.imagetoolbox.texture_generation.data

import com.t8rin.imagetoolbox.texture_generation.domain.model.PatternTextureType

internal fun patternShader(type: PatternTextureType): String {
    val body = when (type) {
        PatternTextureType.RandomTriangles -> """
            result=color1;
            for(int i=0;i<100;i++){
                if(float(i)>=count)break;
                float n=float(i);
                vec2 a=hash2(vec2(n,1.0))-.5,b=hash2(vec2(n,2.0))-.5,c=hash2(vec2(n,3.0))-.5;
                vec2 v=p-a,u=b-a,w=c-a;
                float cross=u.x*w.y-u.y*w.x;
                float s=(v.x*w.y-v.y*w.x)/(abs(cross)<.00001?.00001:cross),t=(u.x*v.y-u.y*v.x)/(abs(cross)<.00001?.00001:cross);
                if(s>=0.0&&t>=0.0&&s+t<=1.0)result=palette(hash(vec2(n,4.0)));
            }
        """.trimIndent()

        PatternTextureType.HexCubePattern -> """
            vec2 cell=floor(p/vec2(1.73205,1.5)),local=vec2(0.0);
            float nearest=100.0;
            for(int y=-1;y<=1;y++)for(int x=-1;x<=1;x++){
                vec2 c=cell+vec2(float(x),float(y));
                vec2 center=vec2((c.x+mod(c.y,2.0)*.5)*1.73205,c.y*1.5);
                vec2 d=p-center;
                if(dot(d,d)<nearest){
                    nearest=dot(d,d);
                    local=d;
                }
            }
            float angle=fract((safeAngle(local)+TAU*.25)/TAU);
            result=angle<.333333?color1:(angle<.666667?color2:color3);
            vec2 v=abs(local);
            float edge=max(v.x*.866025+v.y*.5,v.y);
            if(edge>1.0-thickness)result=color4;
        """.trimIndent()

        PatternTextureType.SquareLooper -> """
            vec2 q=mat2(.7071,-.7071,.7071,.7071)*p;
            float d=max(abs(q.x),abs(q.y));
            float wave=abs(fract(d-time*.1)-.5);
            result=mix(color1,color2,1.0-step(thickness*.5,wave));
        """.trimIndent()

        PatternTextureType.MoireInterference -> """
            float a=length(p-vec2(.4,0)),b=length(p+vec2(.4,0));
            float v=sin(a*intensity)*sin(b*intensity);
            result=mix(color1,color2,smoothstep(thickness-.6,thickness-.4,v));
        """.trimIndent()

        PatternTextureType.TruchetArcs -> """
            vec2 cell=floor(p),q=fract(p);
            if(hash(cell)>.5)q.x=1.0-q.x;
            float d=min(abs(length(q)-.5),abs(length(q-1.0)-.5));
            result=mix(color1,color2,1.0-smoothstep(thickness*.5,thickness*.5+.015,d));
        """.trimIndent()

        PatternTextureType.CircuitPattern -> """
            vec2 cell=floor(p),q=fract(p)-.5;
            float choice=hash(cell);
            if(choice>.5)q=q.yx;
            if(choice>.75)q.x=-q.x;
            float wire=min(segment(q,vec2(-.5,0),vec2(0,0)),segment(q,vec2(0,0),vec2(0,.5)));
            float terminal=abs(length(q-vec2(.3,-.3))-radius);
            float d=min(wire,terminal);
            result=mix(color1,color2,1.0-smoothstep(thickness,thickness+.01,d));
        """.trimIndent()

        PatternTextureType.RosacePattern -> """
            float d=10.0;
            for(int i=0;i<64;i++){
                if(float(i)>=count)break;
                float angle=float(i)*TAU/count;
                vec2 center=vec2(cos(angle),sin(angle))*radius;
                d=min(d,abs(length(p-center)-radius));
            }
            result=mix(color1,color2,1.0-smoothstep(thickness,thickness+.005,d));
        """.trimIndent()

        PatternTextureType.RosettePattern -> """
            float d=10.0;
            vec2 previous=vec2(radius,0.0);
            for(int i=1;i<=256;i++){
                float angle=float(i)*TAU/256.0;
                float r=radius*(.62+.38*cos(angle*count));
                vec2 current=vec2(cos(angle),sin(angle))*r;
                d=min(d,segment(p,previous,current));
                previous=current;
            }
            result=mix(color1,color2,coverage(d,thickness));
        """.trimIndent()

        PatternTextureType.LightArray -> """
            vec3 light=vec3(0.0);
            float scale=1.0;
            for(int i=0;i<6;i++){
                if(float(i)>=layers)break;
                vec2 q=fract(p*scale)-.5,cell=floor(p*scale);
                float rays=exp(-abs(q.x)*120.0)*exp(-abs(q.y)*5.0)+exp(-abs(q.y)*120.0)*exp(-abs(q.x)*5.0);
                float glow=exp(-dot(q,q)*60.0);
                light+=palette(hash(cell+float(i))).rgb*(rays+glow)*intensity/scale;
                scale*=2.0;
            }
            result=vec4(clamp(color1.rgb+light,0.0,1.0),color1.a);
        """.trimIndent()

        PatternTextureType.VoronoiGems -> """
            vec2 cell=floor(p),chosen=cell,local=vec2(0.0);
            float nearest=100.0;
            for(int y=-1;y<=1;y++)for(int x=-1;x<=1;x++){
                vec2 c=cell+vec2(float(x),float(y));
                vec2 v=p-c-.5-(hash2(c)-.5)*variability;
                float d=dot(v,v);
                if(d<nearest){
                    nearest=d;
                    chosen=c;
                    local=v;
                }
            }
            float facet=floor((safeAngle(local)+TAU*.5)/TAU*6.0);
            result=palette(hash(chosen));
            result.rgb*=1.0-shadows*(.25+.6*hash(chosen+facet));
        """.trimIndent()

        PatternTextureType.CitySkyline -> """
            vec2 q=p;
            result=mix(color1,color2,smoothstep(-.5,.5,q.y));
            float sun=1.0-smoothstep(.13,.135,length(q-vec2(.18,-.18)));
            result=mix(result,color3,sun);
            for(int i=0;i<8;i++){
                if(float(i)>=layers)break;
                float layer=float(i),scale=20.0+layer*9.0,x=floor(q.x*scale);
                float top=.02+layer*.055-hash(vec2(x,layer))*.17;
                if(q.y>top){
                    vec4 building=mix(color2,color1,(layer+1.0)/layers);
                    vec2 win=fract(vec2(q.x*scale*3.0,q.y*90.0));
                    float lit=step(.94,hash(floor(vec2(q.x*scale*3.0,q.y*90.0))+layer));
                    building=mix(building,color4,lit*step(.3,win.x)*step(.3,win.y));
                    result=building;
                }
            }
        """.trimIndent()

        PatternTextureType.WinterLandscape -> """
            vec2 q=p;
            result=mix(color1,color2,clamp(q.y+.5,0.0,1.0));
            float moon=1.0-smoothstep(.05,.055,length(q-vec2(.22,-.3)));
            result=mix(result,color4,moon);
            for(int i=0;i<4;i++){
                float layer=float(i);
                float hill=.03+layer*.12+.09*sin(q.x*5.0+layer*2.0);
                if(q.y>hill)result=mix(color2,color4,layer*.22+.2);
                for(int j=0;j<16;j++){
                    float n=float(j);
                    vec2 r=hash2(vec2(n,layer));
                    float x=r.x*1.3-.65,base=.03+layer*.12+.09*sin(x*5.0+layer*2.0);
                    float height=.06+r.y*.1;
                    float y=q.y-base;
                    if(y<0.0&&y>-height&&abs(q.x-x)<(y+height)*.25)result=mix(color1,color3,.35+layer*.15);
                }
            }
            vec2 snow=vec2(q.x,q.y+time*.05)*60.0;
            vec2 cell=floor(snow),v=fract(snow)-hash2(cell);
            float flake=(1.0-smoothstep(.03,.08,length(v)))*step(.8,hash(cell));
            result=mix(result,color4,flake);
        """.trimIndent()

        PatternTextureType.CircleRipplePattern -> """
            vec2 cell=floor(p),q=fract(p)-.5;
            q.x+=mod(cell.y,2.0)*.5;
            q.x=mod(q.x+.5,1.0)-.5;
            q.y/=shapeAspect;
            float d=length(q);
            result=color1;
            if(d<radius){
                float phase=d/radius*4.0;
                float aa=max(pixelWidth*4.0/radius,.001);
                float ring=1.0-smoothstep(.3-aa,.3+aa,abs(fract(phase)-.5));
                result=mix(color2,color3,ring);
                result=mix(result,color4,coverage(d,radius*.12));
            }
        """.trimIndent()

        PatternTextureType.SquareRipplePattern -> """
            vec2 cell=floor(p),q=fract(p)-.5;
            q+=vec2(sin(q.y*TAU),sin(q.x*TAU))*offset;
            float radius=max(abs(q.x),abs(q.y));
            float phase=radius*8.0;
            vec4 ink=mod(cell.x+cell.y,2.0)<1.0?color2:color3;
            result=mix(color1,ink,1.0-smoothstep(thickness-pixelWidth*8.0,thickness+pixelWidth*8.0,abs(fract(phase)-.5)));
            result=mix(result,color4,coverage(radius,.045));
        """.trimIndent()

        PatternTextureType.HatchPattern -> """
            float d=abs(fract(p.x)-.5);
            result=mix(color1,color2,coverage(d,(1.0-thickness)*.5));
        """.trimIndent()

        PatternTextureType.SmoothHatch -> """
            float v=0.5+0.5*cos(p.x*TAU);
            result=mix(color1,color2,smoothstep(balance-.5/hardness,balance+.5/hardness,v));
        """.trimIndent()

        PatternTextureType.XorPattern -> """
            vec2 cell=floor(abs(p)*8.0);
            float value=0.0,weight=1.0;
            for(int i=0;i<12;i++){
                if(float(i)>=intensity)break;
                value+=mod(mod(cell.x,2.0)+mod(cell.y,2.0),2.0)*weight;
                cell=floor(cell*.5);
                weight*=2.0;
            }
            result=mix(color1,color2,value/max(weight-1.0,1.0));
        """.trimIndent()

        PatternTextureType.SquareSpiralPattern -> """
            vec2 v=fract(p)-.5;
            float d=10.0;
            vec2 a=vec2(.47,.47);
            for(int i=0;i<48;i++){
                if(float(i)>=count*4.0)break;
                float j=float(i);
                float side=mod(j,4.0);
                float r=.47-floor(j/4.0)*.94/(count*2.0);
                vec2 b;
                if(side<.5)b=vec2(-r,r);
                else if(side<1.5)b=vec2(-r,-r);
                else if(side<2.5)b=vec2(r-.94/(count*2.0),-r);
                else b=vec2(r-.94/(count*2.0),r-.94/(count*2.0));
                d=min(d,segment(v,a,b));
                a=b;
            }
            result=mix(color1,color2,1.0-smoothstep(thickness*.235/count,thickness*.235/count+.005,d));
        """.trimIndent()

        PatternTextureType.CrossStitchPattern -> """
            vec2 v=fract(p)-.5;
            float parity=mod(floor(p.x)+floor(p.y),2.0);
            float a=segment(v,vec2(-.36,-.36),vec2(.36,.36));
            float b=segment(v,vec2(-.36,.36),vec2(.36,-.36));
            float width=.025+thickness*.11;
            result=color1;
            for(int i=0;i<2;i++){
                bool first=(float(i)<.5)==(parity<.5);
                float d=first?a:b;
                vec4 yarn=first?color2:color3;
                float relief=sqrt(max(0.0,1.0-d*d/(width*width)));
                yarn.rgb*=1.0-shadows*(1.0-relief)*.7;
                result=mix(result,color1,coverage(d,width+pixelWidth*2.0)*shadows*.5);
                result=mix(result,yarn,coverage(d,width));
            }
        """.trimIndent()

        PatternTextureType.ChromaticPlaidPattern -> """
            float x=floor(p.x),y=floor(p.y);
            vec4 a=palette(hash(vec2(x,0))),b=palette(hash(vec2(y,0)));
            float over=step(.5,fract((p.x+p.y)*12.0));
            result=mix(a,b,over);
            if(fract(p.x)<thickness*.2||fract(p.y)<thickness*.2)result=mix(result,color1,.6);
        """.trimIndent()

        PatternTextureType.PlaidPattern -> """
            vec2 q=fract(p*.25)*4.0;
            float ax=abs(q.x-2.0),ay=abs(q.y-2.0);
            vec4 warp=mix(color1,color2,1.0-smoothstep(.5,.5+pixelWidth,ax));
            vec4 weft=mix(color1,color2,1.0-smoothstep(.5,.5+pixelWidth,ay));
            warp=mix(warp,color3,coverage(abs(ax-1.0),.08+thickness*.12));
            weft=mix(weft,color3,coverage(abs(ay-1.0),.08+thickness*.12));
            warp=mix(warp,color4,coverage(abs(ax-.72),.025));
            weft=mix(weft,color4,coverage(abs(ay-.72),.025));
            float over=mod(floor(p.x*18.0)+floor(p.y*18.0),2.0);
            result=mix(mix(warp,weft,.5),mix(warp,weft,over),.45);
            result.rgb*=.92+.08*cos((p.x+p.y)*TAU*18.0);
        """.trimIndent()

        PatternTextureType.GeneratedWaves -> """
            float band=floor(p.y+sin(p.x*TAU*.3)*variability);
            result=palette(hash(vec2(band,1.0)));
        """.trimIndent()

        PatternTextureType.VoronoiHatch -> """
            vec2 cell=floor(p),chosen=cell;
            float nearest=100.0;
            for(int y=-1;y<=1;y++)for(int x=-1;x<=1;x++){
                vec2 c=cell+vec2(float(x),float(y));
                vec2 v=p-c-.5-(hash2(c)-.5)*variability;
                float d=dot(v,v);
                if(d<nearest){
                    nearest=d;
                    chosen=c;
                }
            }
            float angle=hash(chosen)*TAU;
            float stripe=fract(dot(p,vec2(cos(angle),sin(angle)))*8.0);
            result=mix(color1,color2,step(thickness,stripe));
        """.trimIndent()

        PatternTextureType.LoopPattern -> """
            float d=10.0;
            for(int i=0;i<64;i++){
                if(float(i)>=count)break;
                vec2 center=(hash2(vec2(float(i),1.0))-.5)*1.0;
                d=min(d,abs(length(p-center)-radius*(.5+hash(vec2(float(i),3.0)))));
            }
            result=mix(color1,color2,1.0-smoothstep(thickness*.2,thickness*.2+.005,d));
        """.trimIndent()

        PatternTextureType.ScintillatingGrid -> """
            vec2 q=abs(fract(p+.5)-.5);
            float line=min(q.x,q.y);
            result=mix(color1,color2,1.0-step(thickness,line));
            result=mix(result,color3,1.0-step(radius,length(q)));
        """.trimIndent()

        PatternTextureType.CircleSpiralPattern -> """
            float phase=length(p)-safeAngle(p)/TAU;
            result=mix(color1,color2,step(.5,fract(phase*count)));
        """.trimIndent()

        PatternTextureType.TestChart -> """
            vec2 q=fract(p+.5)*2.0;
            float v;
            if(q.y<1.0&&q.x<1.0)v=step(.5,fract(length(q-.5)*8.0));
            else if(q.y<1.0)v=step(.5,fract((q.x+q.y)*8.0));
            else if(q.x<1.0){
                result=vec4(hsv(vec3(q.x,1.0,2.0-q.y)),1.0);
                gl_FragColor=result;
                return;
            }
            else v=mod(floor(q.x*8.0)+floor(q.y*8.0),2.0);
            result=mix(color1,color2,v);
        """.trimIndent()

        PatternTextureType.BentRows -> """
            float row=floor(p.y);
            float x=p.x+sin(row*1.7)*offset;
            float v=mod(floor(x)+row,2.0);
            result=mix(color1,color2,v);
            if(fract(p.y)<thickness)result=color3;
        """.trimIndent()

        PatternTextureType.DiamondsIllusion -> """
            vec2 q=fract(p)-.5;
            float d=max(abs(q.x),abs(q.y));
            float phase=d*count*4.0;
            if(q.x+q.y>0.0)phase+=.5;
            if(q.x-q.y>0.0)phase+=.5;
            result=mix(color1,color2,step(thickness,fract(phase)));
        """.trimIndent()

        PatternTextureType.LunariaPods -> """
            vec2 grid = vec2(p.x * 4.8 - p.y * 1.1, p.y * 5.7);
            result = mix(color1, color2, 0.25 * layerNoise(p * 11.0));
            for (int j = -1; j <= 1; j++) {
                for (int i = -1; i <= 1; i++) {
                    vec2 tile = floor(grid) + vec2(float(i), float(j));
                    vec2 q = grid - tile - 0.5 - (hash2(tile) - 0.5) * 0.58;
                    float st = abs(q.x - 0.24 * sin(q.y * 2.2 + tile.x));
                    result = mix(result, color2,
                                 reliefBand(st, 0.012) * (1.0 - smoothstep(0.45, 0.7, abs(q.y))));
                    q -= vec2(0.22 * sign(hash(tile + 7.1) - 0.5), -0.12);
                    q = rotatePoint(q, (hash(tile + 4.0) - 0.5) * 1.5);
                    q /= (0.47 + overlap * 0.43) * (1.0 + (hash(tile + 3.2) - 0.5) * sizeVariation);
                    vec2 z = q / vec2(0.32, 0.54);
                    float r = length(z);
                    float inside = 1.0 - smoothstep(0.94, 1.0, r);
                    float fib =
                        reliefBand(repeatDistance(z.x * 4.2 / max(0.27, 1.0 - z.y * z.y * 0.72)), 0.044);
                    result = mix(result, mix(color3, color4, 0.45 + 0.45 * fib),
                                 inside * (1.0 - membraneTransparency * 0.8));
                    result = mix(result, color4, reliefBand(r - 0.97, 0.028) * 0.86);
                    result = mix(result, color2, coverage(length(z - 0.1), 0.13));
                }
            }
        """.trimIndent()

        PatternTextureType.HorsetailStems -> """
            float s = p.x * stemDensity - 0.40 * p.y;
            result = mix(color1, color2, layerNoise(p * 8.0) * 0.35);
            for (int i = -1; i <= 1; i++) {
                float row = floor(s) + float(i);
                float w = hash(vec2(row, 9.0));
                float dx = (s - row - 0.5 - (w - 0.5) * 0.42 - 0.13 * sin(p.y * 6.0 + row)) / stemDensity;
                float yy = p.y / internodeLength + hash(vec2(row, 4.0));
                float y = fract(yy) * internodeLength;
                result = mix(result, mix(color2, color3, w), reliefBand(dx, 0.0033));
                for (int j = 0; j < 4; j++) {
                    float index = float(j);
                    float angle = radians(branchAngle * (0.45 + index * 0.23));
                    float len = internodeLength * (0.73 - index * 0.06);
                    vec2 tip = vec2(sin(angle), cos(angle)) * len;
                    float d = segment(vec2(abs(dx), y), vec2(0.0), tip);
                    result = mix(result, mix(color2, color3, index / 3.0), coverage(d, 0.0015));
                }
                result = mix(result, color4, coverage(length(vec2(dx, y) / vec2(1.8, 0.75)), 0.0037));
            }
        """.trimIndent()

        PatternTextureType.DandelionClocks -> """
            vec2 grid = p * downDensity;
            result = mix(color1, color2, 0.55 * layerNoise(p * 4.0));
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 tile = floor(grid) + vec2(float(i), float(j));
                    vec2 q = grid - tile - 0.5 - 0.80 * (hash2(tile) - 0.5);
                    float v = hash(tile + 5.0), radius = crownRadius * (0.55 + v * 0.76);
                    q = rotatePoint(q, (v - 0.5) * 1.2);
                    float stalk = segment(q, vec2(0.0), vec2(-0.45, 1.35) * radius);
                    result = mix(result, color3, coverage(stalk / downDensity, 0.0011));
                    vec2 n = q / vec2(1.0, 0.65);
                    float r = length(n) / radius, angle = safeAngle(n) / TAU + 0.5;
                    float ray = angle * rayCount + 0.34 * sin(r * 2.5 + v * TAU);
                    float end = 0.56 + hash(tile + floor(ray + 0.5)) * 0.52;
                    float d = repeatDistance(ray) * TAU * length(n) / max(rayCount, 1.0) / downDensity;
                    result = mix(result, color4,
                                 reliefBand(d, 0.00085) * (1.0 - smoothstep(end - 0.07, end, r)) *
                                     (0.48 + 0.48 * v));
                }
        """.trimIndent()

        PatternTextureType.CalicoKoi -> """
            vec2 q = p / patchSize;
            float blot = layerNoise(q * 1.83) + 0.30 * noise(q * 6.2);
            float ink = layerNoise(q * 1.67 + 34.2);
            result = mix(color1, color2,
                         smoothstep(0.86 - scarletCoverage * 0.43, 0.88 - scarletCoverage * 0.43, blot));
            result = mix(result, color3, smoothstep(0.61, 0.64, ink));
            vec2 scale = p * vec2(22.0, 29.0);
            scale.x += mod(floor(scale.y), 2.0) * 0.5;
            vec2 l = fract(scale) - vec2(0.5, 0.08);
            float arc = abs(length(l / vec2(0.59, 0.87)) - 1.0);
            result = mix(result, color4, reliefBand(arc, 0.032) * 0.42);
            result = mix(
                result, color3,
                smoothstep(0.87 - 0.19 * inkFlecks, 0.94 - 0.18 * inkFlecks, noise(p * 154.0)) * inkFlecks);
        """.trimIndent()

        PatternTextureType.ValleyBells -> """
            vec2 grid = p * vec2(3.5, 4.1) + vec2(phaseX, 2.0 * phaseY);
            grid.x += 0.5 * mod(floor(grid.y), 2.0);
            vec2 q = fract(grid) - 0.5;
            q.y = -q.y;
            result = mix(color1, color2, layerNoise(p * 18.0) * 0.23);
            float x = q.x - shootLean * q.y;
            for (int i = 0; i < 2; i++) {
                float dir = float(i) * 2.0 - 1.0;
                vec2 v = rotatePoint(q - vec2(0.1 * dir, -0.04), dir * 0.43);
                float w = max(0.0, 1.0 - pow(abs(v.y) / 0.34, 1.35));
                result = mix(result, mix(color2, color3, 0.20 * float(i)),
                             coverage(abs(v.x) - leafWidth * w * 0.66, 0.0) * step(abs(v.y), 0.34));
            }
            float curve =
                -0.36 + 0.62 * (1.0 - pow(abs(clamp((x + 0.15) / 0.52, 0.0, 1.0) - 0.75), 2.0) * 1.9);
            result = mix(result, color3, reliefBand(q.y - curve, 0.008) * step(-0.15, x) * step(x, 0.37));
            for (int i = 0; i < 8; i++) {
                if (float(i) >= bellCount)
                    break;
                float xx = 0.02 + 0.35 * float(i) / max(1.0, bellCount - 1.0);
                float cy = -0.36 + 0.62 * (1.0 - pow(abs((xx + 0.15) / 0.52 - 0.75), 2.0) * 1.9);
                vec2 e = vec2(x - xx, q.y - cy + 0.064);
                result = mix(result, color3, coverage(segment(e, vec2(0.0), vec2(-0.012, 0.067)), 0.005));
                float petal = max(abs(e.x) - (0.030 - 0.22 * e.y),
                                  max(e.y - 0.019, -e.y - 0.045 - 0.009 * cos(e.x * 115.0)));
                result = mix(result, color4, coverage(petal, 0.0));
            }
        """.trimIndent()

        PatternTextureType.SilkPoppies -> """
            vec2 grid = p / repeatPitch + vec2(3.0 * phaseX, 2.0 * phaseY);
            grid.x += mod(floor(grid.y), 2.0) * 0.5;
            vec2 tile = mod(floor(grid), vec2(3.0, 2.0)), q = fract(grid) - 0.5;
            q -= (hash2(tile) - 0.5) * 0.25;
            result = mix(color1, color2, 0.12 + 0.15 * layerNoise(p * 42.0));
            float stem = abs(q.x - 0.12 * sin(q.y * 4.7 + tile.x * 0.7));
            result = mix(result, color2, reliefBand(stem, 0.016) * smoothstep(-0.02, 0.08, q.y));
            vec2 leaf = rotatePoint(q - vec2(0.09, 0.32), -0.72);
            result = mix(result, color2, coverage(ellipseDistance(leaf, vec2(0.062, 0.18)), 0.0));
            vec2 secondLeaf = rotatePoint(q - vec2(-0.11, 0.24), 0.87);
            result = mix(result, mix(color2, color4, 0.10),
                         coverage(ellipseDistance(secondLeaf, vec2(0.047, 0.14)), 0.0));
            float a = safeAngle(q), r = length(q);
            float petal =
                (0.16 + 0.21 * petalOpening) * (0.84 + 0.13 * cos(5.0 * a + sin(3.0 * a + tile.y)) +
                                                0.047 * sin(11.0 * a + tile.x + tile.y));
            float mask = coverage(r - petal, 0.0);
            vec4 printInk = mix(
                color3, color4,
                clamp(0.25 + 0.45 * r / petal + 0.20 * sin(5.0 * a) - 0.10 * pow(abs(cos(a * 15.0)), 7.0),
                      0.0, 1.0));
            printInk = mix(printInk, color3,
                           0.28 * pow(0.5 + 0.5 * cos(a * 26.0 + 1.9 * sin(a * 4.0) + 5.0 * r), 6.0));
            result = mix(result, printInk, mask);
            result = mix(result, color1, coverage(r - petal * (0.22 + 0.035 * sin(a * 9.0)), 0.0));
            result = mix(result, color2, coverage(r - petal * 0.095, 0.0));
        """.trimIndent()

        PatternTextureType.FlyAgaricGrove -> """
            result = mix(color1, color2, 0.24 + 0.70 * layerNoise(p * 7.0));
            for (int i = 0; i < 3; i++) {
                float size = 13.0 * pow(1.9, float(i));
                vec2 grid = p * size, tile = floor(grid),
                     q = fract(grid) - 0.5 - 0.32 * (hash2(tile) - 0.5);
                float r = flakeSize * (0.45 + hash(tile + 21.0)) *
                          (1.0 + 0.17 * sin(safeAngle(q) * 5.0 + tile.x));
                float mask = coverage((length(q) - r) / size, 0.0) * step(hash(tile + 31.0), fleckDensity);
                result =
                    mix(result, mix(color3, color4, clamp(0.62 - flakeRelief * (q.x + q.y) / r, 0.0, 1.0)),
                        mask);
            }
        """.trimIndent()

        PatternTextureType.BeachPebbles -> """
            result = mix(color1, color2, layerNoise(p * 96.0) * 0.25);
            for (int pass = 0; pass < 2; pass++) {
                float g = pass == 0 ? 35.0 : 7.0;
                vec2 q = p * g, tile = floor(q);
                for (int j = -1; j <= 1; j++)
                    for (int i = -1; i <= 1; i++) {
                        vec2 id = tile + vec2(float(i), float(j));
                        vec2 v = q - id - 0.5 - (hash2(id) - 0.5) * 0.42;
                        float h = hash(id + 18.3);
                        v = rotatePoint(v, h * TAU) * vec2(0.90, 1.17);
                        float rad = (pass == 0 ? 0.24 : 0.43) * (1.0 + sizeVariation * (h - 0.5));
                        float d = length(v) * (1.0 + 0.06 * sin(3.0 * safeAngle(v) + h));
                        float mask = coverage((d - rad) / g, 0.0);
                        if (pass == 0)
                            mask *= step(hash(id + 22.0), finePebbles);
                        vec4 rock = toneRamp(0.15 + h * 0.72);
                        float stripe = abs(v.y + 0.27 * v.x + 0.13 * sin(v.x * 6.2 + h * 3.0));
                        rock = mix(rock, color4,
                                   reliefBand(stripe, 0.014) * step(hash(id + 88.0), veinFrequency));
                        rock.rgb *= 0.84 + 0.24 * noise(p * 225.0) - 0.09 * v.y / rad;
                        result = mix(result, rock, mask);
                    }
            }
        """.trimIndent()

        PatternTextureType.TravertinePools -> """
            float level =
                (p.y + 0.25 * p.x + meander * (layerNoise(p * 4.0) - 0.5) * 1.34) / terraceSpacing;
            float t = fract(level);
            result = mix(color1, color2, 0.25 + 0.65 * smoothstep(0.0, 0.88, t));
            result = mix(result, color3, smoothstep(0.84 - rimWidth * 0.70, 0.91 - rimWidth * 0.70, t));
            result = mix(result, color4, smoothstep(1.0 - rimWidth, 1.0 - rimWidth * 0.55, t));
            result.rgb *= 0.94 + 0.12 * noise(p * 148.0);
        """.trimIndent()

        PatternTextureType.TidepoolStars -> """
            vec2 g = vec2(p.x * 12.0 + 1.3 * p.y, (p.y + 0.13 * sin(TAU * p.x * 0.8)) / rowSpacing);
            result = mix(color1, color2, 0.32 + 0.52 * layerNoise(p * 12.0));
            for (int i = 0; i < 2; i++) {
                float f = i == 0 ? 2.5 : 1.0;
                vec2 v = g * f, tile = floor(v), q = fract(v) - 0.5 - 0.18 * (hash2(tile) - 0.5);
                float r = spineSize * (0.65 + hash(tile + 6.1) * 0.65) * (i == 0 ? 0.64 : 1.0);
                float d = length(q * vec2(1.12, 0.91));
                vec4 spine = mix(color2, color4, clamp(0.70 - (q.x + q.y) * 0.61 / r, 0.0, 1.0));
                result = mix(result, spine, coverage((d - r) * rowSpacing / f, 0.0));
            }
            result = mix(result, color3, skinRoughness * 0.17 * noise(p * 266.0));
        """.trimIndent()

        PatternTextureType.WaveCutCliffs -> """
            float flow =
                (p.y + p.x * 0.20 + 0.038 * sin(p.x * 10.0) + 0.045 * layerNoise(p * 9.0)) / grooveSpacing;
            float d = cos(TAU * flow),
                  wet = smoothstep(0.84 - wetPatches * 0.64, 0.99 - wetPatches * 0.60, layerNoise(p * 8.3));
            result = mix(color2, color3, 0.38 + layerNoise(p * 22.0) * 0.45);
            result = mix(result, color1, (0.26 + 0.69 * channelDepth) * smoothstep(0.25, 0.98, d));
            result =
                mix(result, color4, 0.49 * channelDepth * smoothstep(0.68, 1.0, sin(flow * TAU + 0.96)));
            result = mix(result, color1, wet * (0.24 + 0.25 * max(0.0, d)));
        """.trimIndent()

        PatternTextureType.SailingRegatta -> """
            vec2 q = rotatePoint(p, -0.065);
            float weather = layerNoise(q * vec2(5.5, 15.0)) + 0.11 * noise(q * 118.0);
            float threshold = 0.27 + paintWear * 0.48;
            result = mix(color1, color2, 0.19 + 0.34 * layerNoise(q * vec2(12.0, 95.0)));
            result = mix(result, color4, smoothstep(threshold - 0.038, threshold - 0.014, weather));
            result = mix(result, color3, smoothstep(threshold - 0.007, threshold + 0.012, weather));
            vec2 scratches = q * vec2(4.0, 100.0);
            float row = floor(scratches.y), t = fract(scratches.y) - 0.54;
            float mark = reliefBand(t + 0.05 * sin(scratches.x * 6.0 + row), 0.033);
            mark *= smoothstep(0.87 - scratchDensity * 0.55, 0.97 - scratchDensity * 0.50,
                               noise(vec2(scratches.x, row)));
            result = mix(result, color2, mark * 0.87);
            result.rgb *= 0.94 + 0.11 * noise(q * 225.0);
        """.trimIndent()

        PatternTextureType.BarnacleColony -> """
            result = mix(color1, color2, layerNoise(p * 42.0));
            for (int i = 0; i < 2; i++) {
                float f = i == 0 ? 17.0 : 6.4;
                vec2 grid = p * f, id = floor(grid), q = fract(grid) - 0.5 - 0.13 * (hash2(id) - 0.5);
                float r = (0.31 + 0.14 * crowding) * (1.0 + diameterVariation * (hash(id + 28.0) - 0.6));
                float d = length(q) / r, theta = safeAngle(q);
                float ridges = 0.5 + 0.5 * cos(theta * 13.0 + hash(id) * 3.0);
                vec4 shell =
                    mix(color2, color4, clamp(0.56 + shellRibs * 0.4 * ridges - q.y * 0.5, 0.0, 1.0));
                shell = mix(color1, shell, smoothstep(0.22 + ridges * 0.10, 0.43, d));
                shell = mix(shell, color3, smoothstep(0.80, 1.0, d));
                result = mix(result, shell, coverage((d - 1.0) * r / f, 0.0));
            }
        """.trimIndent()

        PatternTextureType.FlyingPetrels -> """
            vec2 q = rotatePoint(p, -0.29);
            float bend = q.x * 3.0 + 0.19 * sin(q.y * 6.2);
            float axis = fract(bend + 0.5) - 0.5;
            float t = (q.y + abs(axis) * tan(radians(barbAngle)) / 3.0) / barbSpacing;
            float split = smoothstep(0.93 - splitFrequency * 0.47, 0.99 - splitFrequency * 0.47,
                                     noise(vec2(floor(t), floor(bend + 0.5))));
            float fibre = cos(TAU * t + 0.15 * sin(14.0 * axis));
            result = mix(color2, color3, 0.45 + 0.35 * sin(bend * TAU) + 0.12 * fibre);
            result = mix(result, color1, (0.19 + split * 0.49) * smoothstep(0.68, 1.0, fibre));
            result = mix(result, color4, reliefBand(axis, 0.012));
        """.trimIndent()

        PatternTextureType.MoonPhases -> """
            vec2 q = p / clastSize;
            vec3 v = cellSample(q + 0.28 * vec2(noise(q * 1.6), noise(q * 1.6 + 82.0)));
            float dust = layerNoise(p * 37.0), face = v.z * 0.77 + 0.16 * noise(q * 6.3);
            vec4 shard = toneRamp(0.2 + 0.77 * face);
            shard.rgb *=
                0.85 + 0.19 * noise(q * 18.0) + 0.12 * step(0.0, sin(q.x * 3.0 + q.y * 4.2 + v.z * 33.0));
            result = mix(color1, shard, smoothstep(matrixWidth * 0.62, matrixWidth + 0.016, v.y));
            result = mix(result, color2,
                         matrixWidth * 0.20 + dustFraction * smoothstep(0.54, 0.78, dust) * 0.77);
        """.trimIndent()

        PatternTextureType.SiderealTrails -> """
            vec2 q = p - vec2(-0.74, -0.48);
            float r = length(q), ang = safeAngle(q) / TAU;
            float ring = r * starDensity, id = floor(ring + 0.5), tone = hash(vec2(id, 8.0));
            float arc = fract(ang * 2.0 + hash(vec2(id, 4.0)));
            result = mix(color1, color2, 0.27 * layerNoise(p * 4.0));
            float trail = reliefBand(repeatDistance(ring) / starDensity, 0.00065 + 0.00074 * tone);
            float mask = smoothstep(0.0, 0.06, arc) * (1.0 - smoothstep(0.10 + exposureLength * 0.67,
                                                                        0.15 + exposureLength * 0.70, arc));
            result = mix(result, mix(color3, color4, tone),
                         trail * mask * mix(0.72, 0.15 + 0.85 * tone, brightnessSpread));
        """.trimIndent()

        PatternTextureType.OrionChart -> """
            vec2 q = p * starDensity, cell = floor(q);
            float cloud = layerNoise(p / groupRadius);
            result = mix(color1, color2, 0.24 * cloud);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = cell + vec2(float(i), float(j)), pt = id + 0.14 + hash2(id) * 0.72;
                    vec2 local = q - pt;
                    float active = smoothstep(0.37, 0.62, noise(pt / (starDensity * groupRadius)));
                    vec2 next = id + vec2(1.0, 0.0);
                    next += 0.14 + hash2(next) * 0.72;
                    float net = coverage(segment(q, pt, next) / starDensity, 0.00057) *
                                step(length(next - pt), linkLength) * active;
                    result = mix(result, color3, net * 0.88);
                    float rad = mix(0.018, 0.094, (0.12 + 0.88 * active) * hash(id + 18.2));
                    result = mix(result, color4, coverage(length(local) / starDensity, rad / starDensity));
                }
        """.trimIndent()

        PatternTextureType.MeteorShower -> """
            vec2 grid = p * trailDensity;
            result = mix(color1, color2, layerNoise(p * 4.0) * 0.48);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(grid) + vec2(float(i), float(j));
                    vec2 q = grid - id - hash2(id);
                    q = rotatePoint(q, -0.73 + radians(angularSpread) * (hash(id + 7.0) - 0.5));
                    float len = trailLength * (0.32 + hash(id + 42.0));
                    float tail = 1.0 - smoothstep(0.0, len, q.x);
                    result = mix(
                        result, mix(color3, color4, tail),
                        coverage(abs(q.y) / trailDensity, 0.0011) * tail * smoothstep(-0.018, 0.0, q.x));
                    result = mix(result, color4, coverage(length(q) / trailDensity, 0.0015));
                }
        """.trimIndent()

        PatternTextureType.ClockworkOrrery -> """
            vec2 q = p - vec2(-0.82, -0.14);
            float r = length(q), ring = r / 0.145, id = floor(ring), axial = fract(ring);
            float circum = safeAngle(q) / TAU * max(7.0, floor((id + 0.5) * 0.145 * TAU / toothPitch)) +
                           mod(id, 2.0) * 0.5;
            float pos = fract(circum) - 0.5;
            float top = 1.0 - smoothstep(crestWidth * 0.43, crestWidth * 0.55 + 0.01, abs(pos));
            float wall = 1.0 - smoothstep(0.38, 0.45, abs(pos));
            float border = smoothstep(0.09, 0.17, axial) * (1.0 - smoothstep(0.80, 0.93, axial));
            vec4 brass = mix(color1, mix(color2, color4, step(0.0, pos)), 0.33 + 0.62 * toothHeight);
            brass = mix(brass, color3, top);
            result = mix(mix(color1, color2, 0.41 + 0.15 * cos(ring * TAU)), brass, wall * border);
            result.rgb *= 0.94 + 0.09 * noise(p * 191.0);
        """.trimIndent()

        PatternTextureType.CometPlume -> """
            vec2 q = vec2(p.x * 2.2, p.y * streamFrequency);
            float wave = q.y - 0.43 * sin(q.x * 3.3 + 0.65 * sin(q.y * 1.3));
            float row = floor(wave), y = fract(wave) - 0.5;
            float x = fract(q.x + 0.43 * row) - 0.5;
            result = mix(color1, color2, 0.39 * layerNoise(p * 7.0));
            for (int j = 0; j < 11; j++) {
                float i = float(j), offset = (i - 5.0) * 0.029;
                float fibre = y - offset * feathering * (0.4 + 1.2 * (x + 0.5)) -
                              0.06 * sin(q.x * 8.0 + i * 0.46 + row);
                float tail = 1.0 - smoothstep(fadeLength * 0.12, fadeLength * 0.66, abs(x));
                result = mix(result, mix(color3, color4, 0.52 * hash(vec2(row, i))),
                             reliefBand(fibre / streamFrequency, 0.00094) * tail * 0.76);
            }
        """.trimIndent()

        PatternTextureType.BinarySunset -> """
            vec2 unitPoint = textureCoordinate - 0.5;
            vec2 q = unitPoint * aspect;
            float landscapeY = unitPoint.y;
            float hillAA = pixelWidth / aspect.y;

            vec2 separation =
                vec2(0.192, -0.063) * (((sunDistance - 20.2071769) * 0.01) / 0.202071769) * 0.5;
            vec2 largeSun = vec2(0.16, -0.16) + separation;
            vec2 smallSun = vec2(-0.032, -0.097) - separation;
            largeSun.y += -0.1285 * (aspect.y - 1.0);
            smallSun.y += -0.1285 * (aspect.y - 1.0);
            result = mix(color2, color3, smoothstep(-0.5, 0.33, landscapeY));
            float sunScale = sunDiameter / 21.6;
            result = mix(result, color4, coverage(length(q - largeSun), 0.108 * sunScale));
            result = mix(result, color4, coverage(length(q - smallSun), 0.053 * sunScale));
            for (int i = 0; i < 6; i++) {
                if (float(i) >= ridges)
                    break;
                float layer = float(i) * (3.0 / max(1.0, ridges - 1.0));
                float wave = 0.048 * sin(5.7 * q.x + layer * 2.6);
                float grain = 0.058 * noise(vec2(q.x * (5.0 - layer * 0.7), layer * 19.0));
                float crest =
                    0.08 + 0.093 * layer + wave + grain + (relief * 0.01 - 1.0) * (wave + grain - 0.029);
                vec4 hill = mix(color2, color1, 0.15 + layer * 0.28);
                result = mix(result, hill, smoothstep(crest - hillAA, crest + hillAA, landscapeY));
            }
        """.trimIndent()

        PatternTextureType.PivotFields -> """
            vec2 grid = p / fieldSize, id = floor(grid), q = fract(grid) - 0.5;
            float small = step(0.49, hash(id));
            if (small > 0.5) {
                grid *= 2.0;
                id = floor(grid);
                q = fract(grid) - 0.5;
            }
            float r = length(q), a = fract(safeAngle(q) / TAU + hash(id));
            float furrow = repeatDistance(r * fieldSize / (furrowPitch * (1.0 + small)));
            result = mix(color3, color4, 0.46 + 0.27 * noise(p * 43.0));
            vec4 field =
                mix(color1, color2, 0.22 + 0.39 * hash(id + 3.0) + 0.22 * step(a, sectorAngle / 360.0));
            field = mix(field, color3, reliefBand(furrow, 0.045) * 0.46);
            result = mix(result, field, coverage((r - 0.48) * fieldSize, 0.0));
        """.trimIndent()

        PatternTextureType.AlluvialBends -> """
            float stream =
                p.y * 3.2 + bendRadius * sin(p.x * 8.9 + 0.7 * sin(p.x * 16.0)) + 0.12 * noise(p * 5.0);
            float d = repeatDistance(stream);
            result = mix(color3, color4, 0.46 + 0.18 * layerNoise(p * 33.0));
            float ridges = reliefBand(repeatDistance((d + 0.03 * noise(p * 26.0)) / depositSpacing), 0.075);
            result = mix(result, color2, 0.47 * ridges);
            result = mix(result, mix(color1, color2, 0.22 + 0.52 * d / max(riverWidth, 0.02)),
                         1.0 - smoothstep(riverWidth, riverWidth + 0.016, d));
        """.trimIndent()

        PatternTextureType.RakedSandGarden -> """
            vec2 q = p * stoneFrequency, id = floor(q);
            float nearest = 10.0, tone = 0.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 tile = id + vec2(float(i), float(j));
                    vec2 delta = q - tile - 0.5 - 0.65 * (hash2(tile) - 0.5);
                    float d = length(rotatePoint(delta, hash(tile) * 3.0) * vec2(1.13, 0.80)) - 0.13 -
                              0.12 * hash(tile + 3.0);
                    if (d < nearest) {
                        nearest = d;
                        tone = hash(tile + 9.0);
                    }
                }
            float phase =
                mix(p.y, nearest / stoneFrequency, deflection * exp(-max(nearest, 0.0) * 2.0)) / rakePitch;
            result = mix(color2, color4, 0.60 + 0.27 * cos(TAU * phase));
            result = mix(result, mix(color1, color3, 0.21 + 0.25 * tone + 0.15 * layerNoise(p * 66.0)),
                         coverage(nearest / stoneFrequency, 0.0));
        """.trimIndent()

        PatternTextureType.BalloonFestival -> """
            float curv = p.x + 0.042 * sin(6.0 * p.y) + 0.021 * sin(10.0 * p.y + p.x * 4.0);
            float g = curv / goreWidth, t = fract(g), strip = mod(floor(g), 3.0);
            result = strip < 1.0 ? color2 : (strip < 2.0 ? color3 : color4);
            result.rgb *=
                0.78 + 0.27 * goreFullness * sin(t * 3.14159) + 0.07 * noise(p * vec2(190.0, 51.0));
            float seam = repeatDistance(g) * goreWidth;
            result = mix(result, color1, 0.75 * reliefBand(seam, 0.0025));
            result = mix(
                result, color3,
                0.85 * reliefBand(seam - 0.005, 0.0011) * smoothstep(0.14, 0.24, fract(p.y / stitchPitch)));
        """.trimIndent()

        PatternTextureType.HarborLight -> """
            float phase = p.x / flutePitch + phaseX + 0.030 * sin(7.0 * p.y);
            float ridge = sin(TAU * phase);
            vec2 refracted = p + vec2(ridge * refraction * 0.105, refraction * 0.013 * cos(TAU * phase));
            float scene = layerNoise(refracted * vec2(3.3, 4.0) + vec2(13.0, 7.0));
            result = mix(color1, color2, smoothstep(0.23, 0.57, scene));
            result = mix(result, color4, smoothstep(0.48, 0.70, scene + 0.16 * refracted.x));
            result = mix(result, color3,
                         pow(max(0.0, cos(TAU * phase - 0.7)), 6.0) * (0.31 + 0.52 * refraction));
            result.rgb *= 0.91 + 0.12 * ridge;
        """.trimIndent()

        PatternTextureType.PaperLanterns -> """
            float g = (p.y + 0.026 * cos(p.x * 6.0)) / ribPitch + phaseX;
            float r = repeatDistance(g), fibre = layerNoise(p * vec2(61.0, 33.0));
            result = mix(color2, color4, 0.24 + translucency * 0.51 + 0.17 * sin(g * TAU));
            result = mix(
                result, color1,
                reliefBand(r * ribPitch, 0.0026 + paperDensity * 0.0035) * (0.40 + 0.43 * paperDensity));
            result = mix(result, color3, paperDensity * fibre * 0.45);
            result.rgb *= 0.91 + 0.18 * noise(p * 175.0);
        """.trimIndent()

        PatternTextureType.ParasolMarket -> """
            vec2 grid = p * 4.1;
            result = mix(color1, color2, layerNoise(p * 21.0) * 0.24);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(grid) + vec2(float(i), float(j));
                    vec2 q = grid - id - 0.5 - (hash2(id) - 0.5) * 0.49;
                    float radius = (0.40 + 0.25 * overlap) * (0.79 + 0.36 * hash(id + 27.0));
                    float g = (safeAngle(q) / TAU + 0.5) * panelCount, t = fract(g) - 0.5;
                    float d = length(q) * cos(t * TAU / panelCount) - radius;
                    vec4 silk = toneRamp(hash(id) * 0.76 + 0.12);
                    silk.rgb *= 0.78 + 0.32 * (0.5 + pleatDepth * t);
                    silk = mix(silk, color1, 0.44 * reliefBand(repeatDistance(g) * length(q), 0.004));
                    result = mix(result, silk, coverage(d / 4.1, 0.0));
                }
        """.trimIndent()

        PatternTextureType.BackgammonPoints -> """
            vec2 g = p * vec2(5.0, 3.5) + vec2(phaseX, 2.0 * phaseY);
            g.x += 0.5 * mod(floor(g.y), 2.0);
            vec2 q = fract(g);
            float wedge =
                coverage(max(abs(q.x - 0.5) - 0.5 * insetWidth * max(0.0, 1.0 - q.y / wedgeLength),
                             q.y - wedgeLength) /
                             5.0,
                         0.0);
            float grain1 = layerNoise(p * vec2(7.0, 98.0)), grain2 = layerNoise(p * vec2(96.0, 9.0));
            result = mix(mix(color3, color4, 0.4 + grainContrast * (grain1 - 0.4)),
                         mix(color1, color2, 0.38 + grainContrast * (grain2 - 0.35)), wedge);
        """.trimIndent()

        PatternTextureType.DominoTable -> """
            vec2 g = p * vec2(5.0 / inlayAspect, 5.0);
            g.x += 0.41 * floor(g.y);
            vec2 q = fract(g) - 0.5, id = floor(g);
            float edge = min(0.5 - abs(q.x), 0.5 - abs(q.y));
            vec4 bone =
                mix(color3, color4, 0.37 + 0.42 * hash(id) + 0.13 * layerNoise(p * vec2(14.0, 72.0)));
            result = mix(color1, bone, smoothstep(jointWidth, jointWidth + 0.014, edge));
            vec2 peg = q - vec2(0.31, 0.30) * sign(hash2(id) - 0.5);
            result =
                mix(result, color2, coverage(length(peg * vec2(inlayAspect, 1.0)) / 5.0, pegSize / 5.0));
        """.trimIndent()

        PatternTextureType.PlayingCardSuits -> """
            vec2 g = p * 7.0 + 6.0 * vec2(phaseX, phaseY);
            g.x += 0.5 * mod(floor(g.y), 2.0);
            vec2 id = floor(g), q = fract(g) - 0.5;
            float mode = mod(id.x + id.y, punchShape), r = 0.43 - bridgeWidth * 0.53;
            float d = mode < 0.5 ? abs(q.x) + abs(q.y) - r : length(q) - r * 0.79;
            if (mode > 1.5)
                d = max(abs(q.x) * 0.86 + abs(q.y) * 0.5, abs(q.y)) - r * 0.77;
            result = mix(color3, color4, 0.42 + 0.34 * layerNoise(p * 28.0));
            vec4 hole = mix(color1, color2, step(0.5, mod(id.x + 2.0 * id.y, 3.0)));
            hole.rgb *= 1.0 - shadowDepth * 0.67 * smoothstep(-0.12, 0.0, d) * (0.51 - q.y);
            result = mix(result, hole, coverage(d / 7.0, 0.0));
        """.trimIndent()

        PatternTextureType.VinylCrate -> """
            vec2 q = p - vec2(-0.86, 0.71);
            float r = length(q), a = safeAngle(q);
            float groove = r / groovePitch + grooveModulation * 0.32 * sin(a * 76.0 + r * 23.0) + a / TAU;
            float shimmer = pow(0.5 + 0.5 * cos(5.0 * a - 1.3), 2.0);
            result = mix(color1, color2, 0.54 + 0.36 * cos(groove * TAU));
            result = mix(result, color3,
                         (0.39 + 0.58 * shimmer) * (0.24 + 0.76 * pow(0.5 + 0.5 * cos(TAU * groove), 2.2)));
            float speck = smoothstep(0.94 - dustSpecks * 0.12, 0.98 - dustSpecks * 0.12, noise(p * 303.0));
            result = mix(result, color4, speck * dustSpecks);
        """.trimIndent()

        PatternTextureType.MusicBoxRoll -> """
            vec2 g = vec2(p.x * 13.0 + phaseX * 8.0, p.y * trackCount),
                 id = vec2(mod(floor(g.x), 8.0), floor(g.y)), q = fract(g) - 0.5;
            result = mix(color3, color4, layerNoise(p * 45.0));
            float voice = hash(vec2(id.y, 0.0)), lengthFactor = slotLength * (0.4 + 0.6 * voice);
            float d = length(vec2(max(0.0, abs(q.x) - lengthFactor * 0.43), q.y * 0.8)) - 0.095;
            result = mix(result, mix(color1, color2, 0.22),
                         coverage(d / trackCount, 0.0) * step(hash(id), punchDensity));
            result = mix(result, color2, 0.24 * reliefBand((0.5 - abs(q.y)) / trackCount, 0.0009));
        """.trimIndent()

        PatternTextureType.ZipperTape -> """
            vec2 q = p * vec2(3.5, 1.0);
            float x = fract(q.x) - 0.5, side = sign(x);
            float phase = p.y / toothPitch + phaseX + 0.5 * step(x, 0.0), y = fract(phase) - 0.5;
            result = mix(color1, color2, 0.22 + 0.14 * cos(q.x * 180.0) * cos(p.y * 330.0));
            result = mix(result, color3, 0.34 * smoothstep(0.46, 0.50, abs(x)) * tapeWidth);
            float tape = 1.0 - smoothstep(tapeWidth * 0.5 - 0.015, tapeWidth * 0.5, abs(x));
            result = mix(result, color2, 0.18 * tape);
            float d =
                max(abs(y) - 0.32 + 0.45 * abs(x), abs(x - side * 0.065) - (0.070 + toothHeight * 0.11));
            vec4 metal = mix(color3, color4, 0.45 + 0.45 * cos(y * 6.0 - x * 11.0));
            result = mix(result, metal, coverage(d / 8.0, 0.0));
        """.trimIndent()

        PatternTextureType.BobbinLace -> """
            vec2 g = p / meshPitch + vec2(phaseX, 2.0 * phaseY);
            result = mix(color1, color2, 0.22 * layerNoise(p * 11.0));
            for (int i = 0; i < 4; i++) {
                float layer = float(i), dir = mod(layer, 2.0) * 2.0 - 1.0;
                float turn = g.x * dir + g.y + layer * 0.27 +
                             scallopDepth * sin(TAU * (g.x + g.y * 0.5) + layer * 1.57);
                float d = repeatDistance(turn) * meshPitch * 0.70;
                float h = 0.42 + 0.40 * sin((g.x * dir - g.y) * (3.0 * TAU) + layer * 2.0);
                result = mix(result, mix(color3, color4, h), reliefBand(d, threadThickness));
            }
        """.trimIndent()

        PatternTextureType.MarquetryCompass -> """
            vec2 g = p * 3.4 + vec2(phaseX, phaseY), id = floor(g), q = fract(g) - 0.5;
            float a = safeAngle(q), r = length(q), s = fract(a / TAU * pointCount + 0.5) - 0.5;
            float tip = 0.41 / (1.0 + 4.6 * abs(s)), h = layerNoise(p * vec2(15.0, 120.0));
            result = mix(color1, color2, 0.30 + grainContrast * 0.6 * h);
            vec4 star =
                mix(color3, color4,
                    0.2 + 0.58 * step(0.0, s) +
                        grainContrast * 0.21 *
                            noise(rotatePoint(q, floor(a / TAU * pointCount + 0.5) * TAU / pointCount) *
                                  vec2(14.0, 76.0)));
            result = mix(result, star, coverage((r - tip) / 3.4, 0.0));
            result = mix(result, color3, reliefBand(r - 0.45, inlayWidth * 0.65));
        """.trimIndent()

        PatternTextureType.WaffleIron -> """
            vec2 q = rotatePoint(p, -0.35) * 7.2 + vec2(phaseX, phaseY), t = fract(q) - 0.5;
            float edge = 0.5 - max(abs(t.x), abs(t.y));
            float lift = 1.0 - smoothstep(rimWidth * 0.12, rimWidth * 1.3, edge);
            float light = abs(t.x) > abs(t.y) ? t.x : t.y;
            float toasted = layerNoise(p * 21.0);
            result = mix(color2, color3, 0.66 - browning * toasted * 0.67);
            result = mix(result, mix(color3, color4, 0.4 + 0.52 * light), lift);
            result = mix(result, color1, cellDepth * 0.44 * smoothstep(0.15, 0.36, light) * (1.0 - lift));
            result.rgb *= 0.91 + 0.16 * noise(p * 145.0);
        """.trimIndent()

        PatternTextureType.CitrusWheels -> """
            vec2 z = p - vec2(-0.90, -0.15);
            float angle = safeAngle(z), r = length(z);
            vec2 g = vec2(r / vesicleSize * 0.49, angle / vesicleSize * 2.6);
            vec3 cell = cellSample(g + 0.18 * vec2(noise(g * 0.76), noise(g * 0.79 + 7.0)));
            result = mix(color1, color3, 0.34 + 0.49 * cell.z + 0.10 * noise(p * 94.0));
            result =
                mix(result, color4, (1.0 - smoothstep(0.02, 0.12, cell.y)) * (0.34 + 0.49 * translucency));
            result = mix(result, color2, 0.21 * translucency * layerNoise(p * 21.0));
            result = mix(result, color4, reliefBand(repeatDistance(angle * 2.7), membraneWidth) * 0.82);
        """.trimIndent()

        PatternTextureType.SaltedPretzels -> """
            float bake = layerNoise(p * 8.0), foam = cellSample(p * 18.0).x;
            result = mix(color2, color3, 0.30 + 0.58 * bake);
            result = mix(result, color1, toasting * smoothstep(0.26, 0.78, bake) * 0.75);
            result = mix(result, color1, 0.42 * blisterDensity * (1.0 - smoothstep(0.12, 0.36, foam)));
            vec2 g = p / (saltSize * 3.0), id = floor(g), q = fract(g) - 0.5 - (hash2(id) - 0.5) * 0.35;
            q = rotatePoint(q, hash(id + 73.0) * 4.0);
            float d = max(abs(q.x) * 1.13, abs(q.y)) - 0.13 - 0.14 * hash(id + 4.0);
            result = mix(result, mix(color3, color4, 0.45 + step(q.x, q.y) * 0.50),
                         coverage(d * saltSize, 0.0) * step(hash(id), 0.30));
        """.trimIndent()

        PatternTextureType.LiquoriceAllsorts -> """
            vec2 g = p / sliceSize, tile = floor(g), q = fract(g) - 0.5;
            float winner = 10.0;
            vec4 best = color1;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = tile + vec2(float(i), float(j));
                    vec2 local = g - id - 0.5 - (hash2(id) - 0.5) * 0.58;
                    float distance2 = dot(local, local);
                    if (distance2 >= winner)
                        continue;
                    winner = distance2;
                    local = rotatePoint(local, hash(id + 11.0) * 2.5);
                    vec4 sweet = mix(color2, color4, hash(id + 5.0));
                    float t = fract((local.y + 0.5) * layerCount);
                    best = mix(color1, sweet, smoothstep(0.39, 0.43, t));
                    if (hash(id + 2.1) < ringFraction)
                        best = mix(color3, color1, smoothstep(0.15, 0.17, length(local)));
                }
            result = best;
        """.trimIndent()

        PatternTextureType.RavioliStamp -> """
            vec2 g = p * 3.3 + vec2(phaseX, phaseY), q = fract(g) - 0.5;
            float edge = 0.5 - max(abs(q.x), abs(q.y));
            float u = abs(q.x) > abs(q.y) ? q.y : q.x;
            float crimp = cos(TAU * u / (crimpPitch * 3.3));
            float flank = 1.0 - smoothstep(0.028, 0.17, edge);
            result = mix(color2, color3, 0.42 + 0.30 * layerNoise(p * 13.0));
            result = mix(result, color1, flank * pressDepth * (0.28 + 0.48 * (0.5 + 0.5 * crimp)));
            result = mix(result, color4, flank * 0.43 * (0.5 + 0.5 * sin(TAU * u / (crimpPitch * 3.3))));
            result = mix(result, color4, flourDust * smoothstep(0.60, 0.85, noise(p * 254.0)) * 0.76);
        """.trimIndent()

        PatternTextureType.CameoBrocade -> """
            vec2 g = p * vec2(3.6, 2.9) + vec2(phaseX, 2.0 * phaseY);
            g.x += 0.5 * mod(floor(g.y), 2.0);
            vec2 q = (fract(g) - 0.5) / medallionSize;
            float d = length(q / vec2(0.37, 0.47)), a = safeAngle(q);
            float silk = 0.5 + 0.5 * cos(TAU * (p.x * 0.8 + p.y) / ribPitch);
            result = mix(color1, color2, 0.14 + 0.15 * silk);
            result = mix(result, color3, coverage((d - 0.88) / 8.0, 0.0));
            result = mix(result, color4, reliefBand(d - 0.93 - 0.025 * cos(28.0 * a), 0.042));
            float head = ellipseDistance(q - vec2(-0.008, -0.095), vec2(0.098, 0.135));
            head =
                min(head, ellipseDistance(rotatePoint(q - vec2(0.055, -0.093), -0.32), vec2(0.071, 0.039)));
            head = min(head,
                       triangleDistance(q, vec2(0.055, -0.140), vec2(0.142, -0.074), vec2(0.067, -0.063)));
            head = min(head, ellipseDistance(q - vec2(0.067, -0.047), vec2(0.043, 0.046)));
            head = min(head, ellipseDistance(q - vec2(-0.035, 0.119), vec2(0.162, 0.065)));
            head = min(head, ellipseDistance(q - vec2(-0.025, 0.015), vec2(0.054, 0.11)));
            result = mix(result, mix(color3, color4, 0.57 + 0.36 * reliefHeight), coverage(head, 0.0));
            result.rgb *= 0.95 + 0.07 * silk;
        """.trimIndent()

        PatternTextureType.BowTiePatchwork -> """
            vec2 g = p * 4.4 + 3.0 * vec2(phaseX, phaseY), id = mod(floor(g), 3.0), q = fract(g) - 0.5;
            q = rotatePoint(q, 0.34 + 1.5708 * mod(id.x + id.y, 2.0));
            float x = abs(q.x), y = abs(q.y);
            float edge = max(y - x * 0.67 - knotWidth * 0.28, x - 0.425), patch = coverage(edge / 4.4, 0.0);
            float h = hash(id);
            vec4 cloth =
                mix(color2, mix(color1, color3, step(0.5, h)), fabricVariation * (0.22 + 0.70 * h));
            float gingham = 0.5 + 0.5 * cos(q.x * 88.0) * cos(q.y * 83.0);
            cloth = mix(cloth, color3, 0.31 * gingham);
            result = mix(color4, cloth, patch);
            result = mix(result, color2, coverage((max(x, y) - knotWidth * 0.55) / 4.4, 0.0));
            float stitch = reliefBand((abs(edge) - 0.016) / 4.4, 0.0014) *
                           step(0.40, fract((x + y) / (stitchLength * 4.4)));
            result = mix(result, color3, stitch * 0.80);
            result.rgb *= 0.96 + 0.068 * noise(p * vec2(157.0, 209.0));
        """.trimIndent()

        PatternTextureType.TasselTrim -> """
            vec2 g = p * vec2(7.8, rowDensity) + vec2(phaseX, 2.0 * phaseY);
            g.x += 0.5 * mod(floor(g.y), 2.0);
            vec2 q = fract(g) - vec2(0.5, 0.13);
            float y = q.y / tasselLength;
            float w = 0.033 + flare * 0.20 * clamp(y, 0.0, 1.0);
            result = mix(color1, color2, 0.18 * layerNoise(p * 37.0));
            float warp = q.x / max(w, 0.012);
            float strands = 0.5 + 0.5 * cos(warp * 43.0 + y * 3.0);
            float end = 1.0 + 0.055 * cos(warp * 40.0 + 2.0);
            float mask = coverage(max(abs(q.x) - w, max(-y, y - end)) / 7.8, 0.0);
            result = mix(result, mix(color2, color4, 0.21 + 0.54 * strands), mask);
            float bead = ellipseDistance(q - vec2(0.0, 0.045), vec2(0.078, 0.071));
            result = mix(result, color3, coverage(bead / 7.8, 0.0));
        """.trimIndent()

        PatternTextureType.MillefioriCanes -> """
            vec2 g = p * 5.8;
            g.x += glassCompression * g.y * 0.37;
            g += glassCompression * 0.55 * vec2(noise(g * 0.61), noise(g * 0.62 + 9.0));
            vec2 base = floor(g), q = vec2(0.0);
            float nearest = 10.0, ident = 0.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = base + vec2(float(i), float(j)),
                         local = g - id - 0.5 - diameterVariation * (hash2(id) - 0.5);
                    float d = dot(local, local);
                    if (d < nearest) {
                        nearest = d;
                        q = local;
                        ident = hash(id);
                    }
                }
            float a = safeAngle(q), r = length(q),
                  flower = 0.26 + 0.077 * cos(petalCount * a + ident * 2.0);
            result = mix(color1, color4, ident * 0.68);
            result = mix(result, color3, reliefBand(r - 0.43, 0.025));
            result = mix(result, color2, coverage((r - flower) / 5.8, 0.0));
            result = mix(result, color3, reliefBand(r - flower, 0.024));
            result = mix(result, color4, coverage((r - 0.098) / 5.8, 0.0));
        """.trimIndent()

        PatternTextureType.GreekMeander -> """
            vec2 q = fract(vec2(p.x / meanderPitch, p.y / borderHeight) + vec2(phaseX, phaseY));
            float d = segment(q, vec2(0.0, 0.28), vec2(0.76, 0.28));
            d = min(d, segment(q, vec2(0.76, 0.28), vec2(0.76, 0.71)));
            d = min(d, segment(q, vec2(0.76, 0.71), vec2(0.24, 0.71)));
            d = min(d, segment(q, vec2(0.24, 0.71), vec2(0.24, 0.45)));
            d = min(d, segment(q, vec2(0.24, 0.45), vec2(0.57, 0.45)));
            d = min(d, segment(q, vec2(0.57, 0.45), vec2(0.57, 0.58)));
            d = min(d, segment(q, vec2(0.57, 0.58), vec2(0.43, 0.58)));
            d = min(d, segment(q, vec2(0.91, 0.80), vec2(0.91, 0.28)));
            d = min(d, segment(q, vec2(0.91, 0.28), vec2(1.0, 0.28)));
            result = mix(color1, color2, 0.11 + 0.17 * noise(p * 93.0));
            result = mix(result, color4, coverage((d - lineWidth * 0.52) * meanderPitch, 0.0));
            result = mix(result, color3, reliefBand(q.y - 0.10, 0.022) + reliefBand(q.y - 0.90, 0.022));
        """.trimIndent()

        PatternTextureType.PaperChainGarland -> """
            vec2 g = p * vec2(4.2, 9.0) + vec2(phaseX, 2.0 * phaseY);
            g.x += 0.5 * mod(floor(g.y), 2.0);
            vec2 q = fract(g) - 0.5;
            float u = abs(q.x) * 2.0 / cutLength;
            float opening = slitOpening * max(0.0, 1.0 - pow(u, 1.65)) * (1.0 - bridgeWidth);
            float d = abs(q.y) - opening;
            float ridge = exp(-abs(d) * 17.0);
            result = mix(color3, color4, 0.36 + 0.30 * layerNoise(p * 47.0));
            result = mix(result, color2, ridge * (q.y < 0.0 ? 0.49 : 0.12));
            result = mix(result, color4, ridge * step(0.0, q.y) * 0.80);
            result = mix(result, color1, coverage(max(d, u - 1.0) / 9.0, 0.0));
        """.trimIndent()

        PatternTextureType.RibbonCockade -> """
            vec2 g = p * 2.8, base = floor(g), q = vec2(0.0);
            float best = 10.0, tone = 0.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = base + vec2(float(i), float(j)), v = g - id - 0.5 - 0.68 * (hash2(id) - 0.5);
                    float d = dot(v, v);
                    if (d < best) {
                        best = d;
                        q = v;
                        tone = hash(id);
                    }
                }
            float arc = fract(safeAngle(q) / TAU * pleatCount + tone), h = 1.0 - abs(arc * 2.0 - 1.0);
            result =
                mix(mix(color1, color2, 0.24 + 0.43 * tone), color3, 0.34 + foldDepth * (h - 0.28) * 0.73);
            result = mix(result, color4, smoothstep(1.0 - highlightWidth, 1.0, h) * 0.44);
            result.rgb *= 0.91 + 0.15 * layerNoise(p * 41.0);
        """.trimIndent()

        PatternTextureType.VenetianShutters -> """
            float t = fract(p.y / slatWidth + phaseX), angle = radians(slatTilt);
            float blade = 1.0 - smoothstep(1.0 - lightGap - 0.014, 1.0 - lightGap, t);
            float gloss = sin((t / (1.0 - lightGap)) * 0.94 * 3.14159 + angle);
            result = mix(color1, mix(color2, color3, clamp(0.22 + 0.74 * gloss, 0.0, 1.0)), blade);
            result = mix(result, color4, 0.54 * reliefBand(t - 0.029, 0.018) * sin(angle));
            result.rgb *= 0.96 + 0.07 * noise(p * vec2(22.0, 184.0));
        """.trimIndent()

        PatternTextureType.SandstoneArcade -> """
            vec2 g = p * vec2(3.1, 3.7) + 4.0 * vec2(phaseX, phaseY);
            g.x += 0.5 * mod(floor(g.y), 2.0);
            vec2 q = fract(g) - vec2(0.5, 0.83);
            float r = length(q), a = safeAngle(vec2(q.x, -q.y));
            float band = step(q.y, 0.0) * smoothstep(archRadius * 0.57, archRadius * 0.59, r) *
                         (1.0 - smoothstep(archRadius, archRadius + 0.013, r));
            vec2 brick = vec2(g.x * 2.0 + 0.5 * floor(g.y * 4.0), g.y * 4.0);
            float mortar = min(repeatDistance(brick.x), repeatDistance(brick.y));
            result =
                mix(color1, mix(color2, color3, 0.43 + 0.42 * hash(mod(floor(brick), vec2(8.0, 16.0)))),
                    smoothstep(jointWidth, jointWidth + 0.013, mortar));
            float joint = min(min(abs(r - archRadius), abs(r - archRadius * 0.57)),
                              repeatDistance(a / 3.14159 * voussoirCount) * r * 3.14159 / voussoirCount);
            vec4 stone =
                mix(color2, color4,
                    0.41 + 0.39 * hash(vec2(floor(a / 3.14159 * voussoirCount), mod(floor(g.y), 4.0))));
            result = mix(result,
                         mix(color1, stone, smoothstep(jointWidth * 0.32, jointWidth * 0.75, joint)), band);
            result.rgb *= 0.93 + 0.14 * noise(p * 180.0);
        """.trimIndent()

        PatternTextureType.GearBlueprint -> """
            vec2 q = rotatePoint(p, radians(helixAngle));
            float phase = fract(q.y / threadPitch + phaseX);
            float flank = smoothstep(0.16, 0.16 + chamferWidth, phase) *
                          (1.0 - smoothstep(0.71, 0.71 + chamferWidth, phase));
            result = mix(color1, color3, flank);
            result = mix(result, color4,
                         smoothstep(0.15, 0.21, phase) *
                             (1.0 - smoothstep(0.21, 0.21 + chamferWidth * 0.64, phase)) * 0.87);
            result = mix(result, color2, smoothstep(0.67, 0.81, phase) * 0.45);
            result.rgb *= 0.91 + 0.15 * layerNoise(q * vec2(9.0, 121.0));
        """.trimIndent()

        PatternTextureType.AdmissionTickets -> """
            vec2 g = vec2(p.x / holePitch + phaseX * 5.0, p.y * 6.4),
                 tile = vec2(mod(floor(g.x + 0.5), 5.0), floor(g.y + 0.5)), q = fract(g + 0.5) - 0.5;
            float torn = step(hash(tile + 27.0), tornFraction);
            float slitLength = 0.45 * (1.0 - bridgeWidth);
            float dist = length(vec2(max(abs(q.x) - slitLength, 0.0) * holePitch, q.y / 6.4)) - 0.003;
            float fibre = torn * 0.0024 * noise(p * 287.0);
            result = mix(color3, color4, 0.42 + 0.35 * layerNoise(p * 18.0));
            result = mix(result, color2, coverage(dist - 0.0033 - fibre, 0.0));
            result = mix(result, color1, coverage(dist + fibre, 0.0));
        """.trimIndent()

        PatternTextureType.SawtoothRoofscape -> """
            vec2 g = vec2(p.x * 5.3 - p.y * tan(radians(roofAngle)) * 3.2 + phaseX, p.y * 3.2);
            float t = fract(g.x), frost = layerNoise(p * 25.0);
            result =
                mix(color2, color3, clamp(0.13 + 0.70 * t + 0.23 * glassFrost * (frost - 0.25), 0.0, 1.0));
            result =
                mix(result, color4, 0.42 * smoothstep(0.74, 0.90, t) * (1.0 - smoothstep(0.95, 1.0, t)));
            float edge = min(repeatDistance(g.x) / 5.3, repeatDistance(g.y) / 3.2);
            result = mix(result, color1, reliefBand(edge, mullionWidth * 0.54));
        """.trimIndent()

        PatternTextureType.FanVault -> """
            vec2 g = p * 3.3 + vec2(phaseX, phaseY), q = fract(g) - 0.5;
            result = mix(color2, color3, 0.40 + 0.18 * layerNoise(p * 23.0));
            float dist = 1.0;
            for (int j = 0; j < 2; j++)
                for (int i = 0; i < 2; i++) {
                    vec2 v = q - vec2(float(i) - 0.5, float(j) - 0.5);
                    float r = length(v);
                    float ang = atan(abs(v.x), max(0.0001, abs(v.y)));
                    float rib = repeatDistance(ang / (1.5708 * fanSpread) * ribCount) * 1.5708 * fanSpread *
                                r / ribCount;
                    dist = min(dist, max(rib, r - 0.79));
                    dist = min(dist, abs(r - 0.57));
                }
            result = mix(result, color1, reliefBand(dist, 0.033) * 0.51 * ribHeight);
            result = mix(result, color4, reliefBand(dist, 0.014) * (0.35 + 0.62 * ribHeight));
        """.trimIndent()

        PatternTextureType.WoodcutSwells -> """
            float drift = layerNoise(p * vec2(3.1, 3.7));
            float broad = layerNoise(p * vec2(2.3, 2.2) + vec2(2.0, -3.0));
            float flow = p.y - 0.25 * p.x + 0.077 * sin(TAU * (1.54 * p.x + 0.39 * p.y + 1.13 * drift)) +
                         0.12 * sin(TAU * (0.74 * p.x - 0.23 * p.y + 0.78 * broad)) + 0.15 * (drift - 0.5);
            float n = flow / hatchPitch,
                  level = layerNoise(vec2(p.x * 5.6 + floor(n) * 0.51, p.y * 6.5 + floor(n) * 0.33));
            float width = cutWidth * (0.61 + 0.68 * level);
            float cut = reliefBand((abs(fract(n) - 0.48) + 0.012 * (noise(p * 135.0) - 0.5)) * hatchPitch,
                                   width * 0.5 * hatchPitch);
            float sliver =
                reliefBand((fract(n) - 0.18 - 0.045 * sin(TAU * level)) * hatchPitch, 0.028 * hatchPitch) *
                smoothstep(0.43, 0.73, level);
            result = mix(mix(color1, color2, layerNoise(p * 13.0)),
                         mix(color3, color4, layerNoise(p * vec2(17.0, 33.0))), max(cut, sliver));
        """.trimIndent()

        PatternTextureType.ApartmentPlan -> """
            vec2 g = vec2(p.x * 5.2 / slabAspect, p.y * 5.2);
            g.x += floor(g.y) * 0.41;
            vec2 q = fract(g) - 0.5, id = floor(g);
            float edge = min(0.5 - abs(q.x), 0.5 - abs(q.y));
            float chip = edgeChips * 0.036 * noise(p * 77.0);
            result = mix(color1,
                         mix(mix(color2, color3, 0.26 + 0.46 * hash(id + 1.8)), color4,
                             0.20 + 0.32 * hash(id) + 0.18 * layerNoise(p * 67.0)),
                         smoothstep(0.007 + chip, 0.02 + chip, edge));
            float x = q.x - (hash(id + 7.0) - 0.5) * 0.38;
            vec2 lug = vec2(x, (0.5 - abs(q.y)) * sign(q.y));
            float metal = min(segment(lug, vec2(-0.087, -0.055), vec2(0.087, 0.055)),
                              min(segment(lug, vec2(0.087, 0.055), vec2(0.087, 0.094)),
                                  segment(lug, vec2(-0.087, -0.055), vec2(-0.087, -0.094))));
            result =
                mix(result, color1, coverage(metal / 5.2, 0.0043) * step(hash(id + 33.0), crampFrequency));
        """.trimIndent()

        PatternTextureType.RooftopTanks -> """
            vec2 q = rotatePoint(p, -0.11);
            float x = q.x / sheetWidth + 2.0 * phaseX, row = mod(floor(x), 2.0),
                  y = q.y / (sheetWidth * 1.74) + 0.5 * row;
            float ageing = layerNoise(q * 8.0 + vec2(row * 0.4, 0.0)) + 0.12 * noise(q * 49.0);
            float green = smoothstep(0.74 - 0.40 * patinaCoverage, 0.79 - 0.40 * patinaCoverage, ageing);
            result = mix(mix(color1, color2, 0.33 + 0.46 * layerNoise(q * vec2(11.0, 51.0))),
                         mix(color3, color4, 0.27 + 0.54 * noise(q * 33.0)), green);
            float seam = repeatDistance(x) * sheetWidth;
            float join = repeatDistance(y) * sheetWidth * 1.74;
            result = mix(result, color1, 0.73 * reliefBand(seam, 0.0043) + 0.20 * reliefBand(join, 0.0019));
            result =
                mix(result, color4, 0.45 * reliefBand((fract(x + 0.5) - 0.5) * sheetWidth - 0.005, 0.0016));
        """.trimIndent()

        PatternTextureType.BookclothSpines -> """
            vec2 q = rotatePoint(p, 0.045);
            vec2 loom = q / weavePitch;
            float a = cos(TAU * loom.x), b = cos(TAU * loom.y);
            float up = mod(floor(loom.x) + floor(loom.y), 2.0);
            float weave = mix(a, b, up) * (0.17 - 0.10 * coatingThickness);
            result = mix(color1, color2, 0.46 + weave + 0.19 * layerNoise(q * 13.0));
            float fold = repeatDistance(q.x * 4.3 + 0.025 * sin(q.y * 7.0));
            float age = (1.0 - smoothstep(0.005, 0.055, fold)) * (0.3 + 0.7 * noise(q * vec2(35.0, 94.0)));
            result = mix(result, mix(color3, color4, noise(q * 109.0)), age * foldWear);
        """.trimIndent()

        PatternTextureType.HarbourBunting -> """
            vec2 q = rotatePoint(p, -0.35);
            float g = q.y / panelWidth + 4.0 * phaseX, id = mod(floor(g), 4.0), t = fract(g) - 0.5;
            result =
                mix(color3, mix(color1, color2, hash(vec2(id, 0.0))), 0.28 + 0.38 * hash(vec2(id, 4.0)));
            float mesh = 0.5 + 0.5 * cos(q.x * 545.0) * cos(q.y * 585.0);
            result = mix(result, color4, clothTranslucency * (0.09 + 0.38 * mesh));
            float zig = abs(fract(q.x / stitchPitch) - 0.5) * 4.0 - 1.0;
            float seam = abs(abs(t) - 0.42);
            result = mix(result, color1, reliefBand(seam * panelWidth, 0.0070) * 0.65);
            result = mix(result, color4, reliefBand((abs(t) - 0.42) * panelWidth - zig * 0.0048, 0.0018));
        """.trimIndent()

        PatternTextureType.PantographLinkage -> """
            float cs = cos(radians(openingAngle)), sn = sin(radians(openingAngle));
            vec2 g = vec2(p.x * cs + p.y * sn, p.x * cs - p.y * sn) * 6.2 + vec2(phaseX, phaseY);
            vec2 d = vec2(repeatDistance(g.x), repeatDistance(g.y)) / 6.2;
            result = mix(color1, color2, 0.12 + 0.15 * layerNoise(p * 32.0));
            result = mix(result, color2, reliefBand(d.x, barWidth));
            result = mix(result, color3, reliefBand(d.y, barWidth * 0.88));
            float rivet = length(d);
            result = mix(result, color1, coverage(rivet, rivetRadius * 1.2));
            result = mix(result, color4, coverage(rivet, rivetRadius));
        """.trimIndent()

        PatternTextureType.DualTrace -> """
            float y = p.y * 8.0 + phaseX, x = p.x / signalPeriod;
            float wave = 0.29 * sin(TAU * x) + 0.12 * sin(TAU * x * 2.0 + 1.0);
            float wave2 = 0.24 * sin(TAU * (x + tracePhase)) + 0.13 * sin(TAU * (x * 2.0 + tracePhase));
            float d = repeatDistance(y - wave) / 8.0, e = repeatDistance(y - wave2 - 0.38) / 8.0;
            result = mix(color1, color2, 0.40 * afterglow * exp(-min(d, e) * 84.0));
            result = mix(result, color3, reliefBand(d, 0.0015));
            result = mix(result, color4, reliefBand(e, 0.0013));
        """.trimIndent()

        PatternTextureType.StressFringes -> """
            vec2 a = sin((p - vec2(-0.20, 0.10)) * 3.141593) / 3.141593;
            vec2 b = sin((p - vec2(0.21, -0.10)) * 3.141593) / 3.141593;
            float r1 = length(a), r2 = length(b);
            float axis = 2.0 * (safeAngle(a) - safeAngle(b));
            float load = 0.040 / (r1 + 0.045) + 0.050 / (r2 + 0.045);
            float phase = load * intensity + 0.33 * cos(axis);
            float band = 0.5 + 0.5 * sin(TAU * phase);
            result = mix(color1, color2, smoothstep(0.02, 0.55, band));
            result = mix(result, color3, smoothstep(0.45, 0.84, band));
            result = mix(result, color4, smoothstep(0.82, 1.0, band));
            result = mix(result, color1, coverage(min(r1, r2), 0.045));
        """.trimIndent()

        PatternTextureType.WindBarbs -> """
            vec2 grid = p * strokeDensity;
            result = mix(color3, color4, 0.34 + 0.41 * layerNoise(p * 18.0));
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(grid) + vec2(float(i), float(j)), pt = id + hash2(id) * 0.75;
                    vec2 q = grid - pt;
                    vec2 v = pt / strokeDensity;
                    float angle = TAU * noise(v / vortexRadius) + 0.5 * sin(v.y * 12.0);
                    q = rotatePoint(q, angle);
                    float d = segment(q, vec2(-0.5 * hatchLength, 0.0),
                                      vec2(0.5 * hatchLength, 0.07 * hatchLength));
                    result =
                        mix(result, mix(color1, color2, hash(id)), coverage(d / strokeDensity, 0.00092));
                }
        """.trimIndent()

        PatternTextureType.AlignmentNomogram -> """
            vec2 q = rotatePoint(p, 0.33);
            result = mix(color3, color4, 0.35 + 0.40 * layerNoise(p * 34.0));
            for (int i = 0; i < 2; i++) {
                vec2 v = i == 0 ? q : q.yx;
                float curve =
                    v.y * 5.0 + scaleCurvature * (sin(v.x * 6.0 + float(i) * 2.0) + 0.33 * sin(v.y * 6.0));
                float d = (fract(curve + 0.5) - 0.5) / 5.0,
                      period = repeatDistance(v.x / tickPitch) * tickPitch;
                float tick =
                    reliefBand(period, 0.0010) * (1.0 - smoothstep(tickLength * 0.9, tickLength, abs(d)));
                result = mix(result, i == 0 ? color1 : color2, max(reliefBand(d, 0.0012), tick));
            }
        """.trimIndent()

        PatternTextureType.VacuumTriodes -> """
            float line1 = p.x * 7.0 + 0.20 * sin(TAU * p.y + 0.4),
                  line2 = p.x * 7.0 + layerGap + 0.20 * sin(TAU * p.y + 2.0);
            float heat = repeatDistance(line1);
            result = mix(color1, color3, filamentHeat * 0.62 * exp(-heat * 11.0));
            float coil = p.y / helixPitch + phaseX + 0.32 * sin(TAU * line2);
            result = mix(result, color2, reliefBand(repeatDistance(coil) * helixPitch, 0.0019));
            result = mix(result, color2, reliefBand(repeatDistance(line2) / 7.0, 0.0022));
            result = mix(result, color4, reliefBand(heat / 7.0, 0.0021) * filamentHeat);
        """.trimIndent()

        PatternTextureType.BasePairs -> """
            float xx = p.x * 4.4, id = floor(xx), x = fract(xx) - 0.5;
            float phase = p.y / helixPitch + phaseX + id * neighbourPhase;
            float shift = 0.32 * sin(TAU * phase);
            float r = repeatDistance(phase * rungFrequency);
            result = mix(color1, color2, 0.18 * layerNoise(p * 32.0));
            result = mix(result, color2, reliefBand(r, 0.075) * step(abs(x), abs(shift)));
            float first = reliefBand((x - shift) / 4.4, 0.0028),
                  second = reliefBand((x + shift) / 4.4, 0.0028);
            result = mix(result, color3, first);
            result = mix(result, color4, second * (1.0 - 0.67 * first * step(0.0, cos(TAU * phase))));
        """.trimIndent()

        PatternTextureType.HarmonicSpectrogram -> """
            float x = p.x, y = fract(p.y + 0.5);
            float pitch = 0.052 + 0.010 * sin(4.2 * x) + 0.012 * noise(vec2(x * 4.0, 0.0));
            float order = y / pitch;
            float line = abs(fract(order + 0.5) - 0.5);
            float partial = exp(-line * line * (65.0 + 65.0 * y));
            float articulation = 0.38 + 0.62 * noise(vec2(x * 15.0, floor(order + 0.5) * 0.45));
            float formant = 0.21 + 0.65 * pow(abs(sin(y * 6.4 + sin(x * 3.2))), 2.0);
            float burst = pow(noise(vec2(x * 23.0, 0.5)), 5.0) * exp(-y * 1.8);
            float power = 0.08 * noise(vec2(x * 144.0, y * 93.0)) + burst;
            power += partial * articulation * formant * (1.32 - 0.64 * y) * intensity;
            result = mix(color1, color2, smoothstep(0.025, 0.26, power));
            result = mix(result, color3, smoothstep(0.22, 0.52, power));
            result = mix(result, color4, smoothstep(0.44, 0.78, power));
        """.trimIndent()

        PatternTextureType.GelBands -> """
            vec2 g = vec2(p.x * laneCount, p.y * 20.0);
            vec2 id = floor(g), q = fract(g) - 0.5;
            q.y -= 0.24 * (hash(id) - 0.5) + 0.13 * q.x * q.x;
            float t = max(abs(q.x) / (bandWidth * 0.48), abs(q.y) / (0.09 + 0.14 * bandBlur));
            float mark = exp(-t * t * (2.0 - 1.15 * bandBlur)) * step(hash(id + 33.0), 0.49);
            result = mix(color1, color2, 0.17 + 0.25 * layerNoise(p * vec2(31.0, 4.0)));
            result = mix(result, mix(color3, color4, hash(id + 17.0)), mark);
        """.trimIndent()

        PatternTextureType.Stereonet -> """
            vec2 g = p * arcCount + vec2(phaseX, phaseY);
            vec2 w = g + curvature * vec2(0.86 * sin(TAU * p.y) + 0.39 * sin(TAU * (p.x + p.y)),
                                          0.79 * sin(TAU * p.x));
            vec2 d = vec2(repeatDistance(w.x), repeatDistance(w.y)) / arcCount;
            result = mix(color1, color2, 0.26 * layerNoise(p * 14.0));
            result = mix(result, color3, reliefBand(d.x, threadWidth * 0.90));
            result = mix(result, color4, reliefBand(d.y, threadWidth));
        """.trimIndent()

        PatternTextureType.PressureDials -> """
            vec2 g = p * 5.4 + vec2(phaseX, 2.0 * phaseY);
            result = mix(color1, color2, 0.4);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    float row = floor(g.y) + float(j);
                    float stagger = mod(row, 2.0) * 0.5;
                    vec2 c = vec2(floor(g.x - stagger) + float(i) + stagger, row);
                    vec2 q = g - c;
                    float radius = 0.63 + 0.55 * spotOverlap, d = length(q);
                    float a = safeAngle(q);
                    float light = 0.5 + 0.5 * cos(a + 1.4);
                    float ring = 0.5 + 0.5 * cos(TAU * d / radius * grindingLines);
                    vec4 metal = mix(mix(color1, color2, 0.18 + 0.74 * ring),
                                     mix(color3, color4, 0.08 + 0.72 * ring), light);
                    result = mix(result, metal, coverage((d - radius) / 5.4, 0.0));
                }
        """.trimIndent()

        PatternTextureType.AirfoilStreamlines -> """
            vec2 q = rotatePoint(p, -0.05);
            float offset = q.y;
            for (int i = -1; i <= 1; i++) {
                float id = floor(q.x * 2.0) + float(i);
                float cx = (id + 0.43 * hash(vec2(id, 1.0))) / 2.0;
                float dy = q.y - 0.29 * sin(id * 2.7);
                float dx = q.x - cx;
                float wake = exp(-abs(dx) / (dx > 0.0 ? wakeLength : 0.08) - dy * dy / 0.025);
                offset += deflection * 0.095 * wake * sign(dy + 0.0001) * smoothstep(0.0, 0.085, abs(dy));
            }
            float t = repeatDistance(offset / linePitch) * linePitch;
            result = mix(color1, color2, 0.28 + 0.29 * layerNoise(p * 9.0));
            result = mix(result, mix(color3, color4, 0.36), reliefBand(t, 0.00155));
        """.trimIndent()

        PatternTextureType.MolecularModel -> """
            vec2 g = p / latticePitch;
            vec3 v = cellSample(g + defectFrequency * 0.75 * vec2(noise(g * 0.63), noise(g * 0.62 + 7.0)));
            result = mix(color3, color4, 0.42 + 0.34 * layerNoise(p * 23.0));
            float edge = 1.0 - smoothstep(0.010, 0.067, v.y);
            result = mix(result, color2, edge);
            vec2 id = floor(g);
            float t = 10.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 tile = id + vec2(float(i), float(j));
                    vec2 site = tile + 0.10 + 0.80 * hash2(tile);
                    float distanceTo = length(g - site);
                    if (hash(tile + 8.0) < 0.18 + defectFrequency * 0.28)
                        t = min(t, distanceTo);
                }
            result = mix(result, color1, coverage(t * latticePitch, nodeSize * 0.62));
        """.trimIndent()

        PatternTextureType.MarshHeron -> """
            result = mix(color1, color2, layerNoise(p * 9.0));
            for (int i = 0; i < 4; i++) {
                float lane = float(i), tilt = (hash(vec2(lane, 12.0)) - .43) * reedTilt * 2.0;
                float x = (p.x + tilt * p.y + lane * 3.4) * reedCount;
                float stem = fract(x + .12 * sin(p.y * 8.0 + floor(x) * .7)) - .5;
                float node = repeatDistance(p.y / internode + hash(vec2(floor(x), lane)));
                float width = .025 + .032 * hash(vec2(floor(x), lane + 5.0)) +
                              .021 * (1.0 - smoothstep(.012, .034, node));
                float edge = 1.0 - smoothstep(width, width + pixelWidth * reedCount, abs(stem));
                vec4 stalk = mix(color3, color4, .19 + .52 * smoothstep(-width, width, stem));
                stalk = mix(stalk, color2, .53 * (1.0 - smoothstep(.01, .03, node)));
                result = mix(result, stalk, edge * (.58 + .10 * lane));
            }
        """.trimIndent()

        PatternTextureType.SwitchbackPass -> """
            vec2 q = rotatePoint(p, slopeGrade * .81) * 8.0 / rubbleSize;
            vec4 rock = surfaceCell(q * vec2(1.05, 1.26), .85);
            vec2 id = floor(q * vec2(1.05, 1.26) - rock.zw);
            vec2 local = rotatePoint(rock.zw, hash(id + 2.0) * 2.3 + slopeGrade * .44);
            float aspect = .74 + .64 * hash(id + 8.0);
            vec2 axis = local * vec2(aspect, 1.0 / aspect);
            float face = max(max(dot(axis, vec2(.94, .30)), -dot(axis, vec2(.87, .49))),
                             max(dot(axis, vec2(-.34, .96)), -dot(axis, vec2(-.50, .87))));
            face = max(face, dot(axis, vec2(-.7, -.71)) * .88);
            face += .027 * (noise(q * 13.0) - .5);
            float radius = .13 + .24 * hash(id + 5.0);
            float chips = noise(q * 31.0);
            result = mix(color1, color2, .26 + .62 * layerNoise(q * 4.0));
            vec4 gravel = surfaceCell(q * 4.9 + vec2(0.0, slopeGrade * 0.7 * q.x), .8);
            float fleck = (1.0 - smoothstep(.12, .23, gravel.x)) *
                          smoothstep(1.0 - gravelShare, 1.0, noise(q * 1.2) + .33);
            result = mix(result, color3, fleck * .87);
            vec4 slab = mix(color2, color4,
                            .15 + .33 * hash(id) + .19 * chips +
                                .26 * smoothstep(-.012, .018, local.x + .33 * local.y));
            slab = mix(slab, color1,
                       .30 * smoothstep(radius - .06, radius, face) * smoothstep(-.13, .18, local.y));
            result = mix(result, slab, 1.0 - smoothstep(radius, radius + .022, face));
        """.trimIndent()

        PatternTextureType.CicadaAmber -> """
            vec2 q = rotatePoint(p, -0.37);
            q.x += 0.15 * sin(4.0 * q.y + 1.0) + 0.19 * q.y + 0.054 * sin(7.0 * q.y + 3.0 * q.x);
            float rib = q.x * 5.6;
            vec2 honey = vec2(rib * 2.05, q.y * crossVeins + 0.58 * rib) +
                         0.45 * vec2(noise(q * 9.0), noise(q * 8.0 + 8.0));
            vec4 cell = surfaceCell(honey, 0.78);
            float sheenPhase = 0.5 + 0.5 * sin(13.0 * q.y + 15.0 * q.x + 5.0 * layerNoise(q * 7.0));
            float pane = hash(floor(honey - cell.zw));
            vec4 glass = mix(color2, color3, 0.33 + 0.35 * layerNoise(q * 7.0) + 0.3 * pane);
            glass = mix(glass, color4, membraneSheen * (0.15 + 0.62 * sheenPhase));
            float vein = min(cell.y, repeatDistance(rib) * 1.50);
            float network = 1.0 - smoothstep(veinWidth * 0.55, veinWidth + pixelWidth * 6.0, vein);
            result = mix(glass, color1, network);
            result = mix(result, color2, (1.0 - smoothstep(0.0, veinWidth * 0.42, vein)) * 0.65);
        """.trimIndent()

        PatternTextureType.Passionflower -> """
            vec2 q = p / flowerPitch + 2.0 * vec2(phaseX, phaseY);
            q.x += .46 * mod(floor(q.y), 2.0);
            vec2 id = mod(floor(q), 2.0);
            vec2 uv = fract(q) - .5 - .095 * (hash2(id) - .5);
            float angle = safeAngle(uv) + hash(id) * 1.9;
            float d = length(uv);
            float g = layerNoise(p * 19.0);
            result = mix(color1, color2, .19 + .34 * g);
            for (int i = 0; i < 2; i++) {
                float layer = float(i);
                float petals = .5 + .5 * cos(10.0 * (angle + layer * .31));
                float edge = petalLength * (1.0 - .20 * layer) * (.50 + .50 * pow(petals, .42));
                vec4 silk = mix(color2, color4, .31 + .29 * layer + .25 * d);
                result = mix(result, silk, 1.0 - smoothstep(edge - .005, edge + .009, d));
            }
            float corona = pow(max(0.0, cos(angle * filaments + .82 * sin(18.0 * d + 3.0 * angle))), 4.0);
            result = mix(result, mix(color3, color4, .5 + .48 * cos(d * 83.0)),
                         corona * smoothstep(.075, .14, d) * (1.0 - smoothstep(.33, .38, d)));
            result = mix(result, color2, 1.0 - smoothstep(.073, .092, d));
        """.trimIndent()

        PatternTextureType.IcebergKeel -> """
            vec2 q = rotatePoint(p, .32);
            float belt = repeatDistance(q.y / bubbleBands + .13 * sin(q.x * 13.0) + .20 * noise(q * 6.0));
            float cloud = layerNoise(p * 9.0);
            result = mix(color1, color2, .26 + cloud * .63);
            result = mix(result, color3, iceCloud * pow(cloud, 2.0) * 1.32);
            vec2 field = q * 46.0;
            vec4 cell = surfaceCell(field, .88);
            float radius = bubbleSize * (.50 + hash(floor(field - cell.zw)));
            float rim = 1.0 - smoothstep(.012, .053, abs(cell.x - radius));
            float sheen = .24 + .76 * smoothstep(.02, .09, -cell.z - cell.w);
            result = mix(result, color4, rim * sheen * (1.0 - smoothstep(.025, .18, belt)));
        """.trimIndent()

        PatternTextureType.SoundingFlukes -> """
            vec2 q = rotatePoint(p, -.43);
            float lane = fract(q.x * 2.1 + .055 * sin(q.y * 6.0)) - .5;
            float n = layerNoise(p * 15.0);
            float phase = (q.y - wakeAngle * abs(lane) / 2.1 + .012 * (noise(p * 27.0) - .5)) / wakePitch;
            float d = repeatDistance(phase);
            float strength = .20 + .80 * (1.0 - smoothstep(.10, .5, abs(lane)));
            result = mix(color1, color2, .24 + .48 * n + .22 * (.5 + .5 * cos(TAU * phase)));
            result = mix(result, color3, (1.0 - smoothstep(.035, .14, d)) * strength);
            result = mix(result, color4,
                         (1.0 - smoothstep(.009, .055, d)) * strength *
                             smoothstep(.78 - foamCover * .63, .96 - foamCover * .5, noise(p * 120.0)));
        """.trimIndent()

        PatternTextureType.SamaraFlight -> """
            vec2 q = p * seedDensity;
            result = mix(color1, color2, .36 + .49 * layerNoise(p * 27.0));
            for (int j = -1; j <= 1; j++) {
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    vec2 delta = q - id - .5 - .48 * (hash2(id) - .5);
                    float angle = (hash(id + 19.0) - .5) * 5.6 * angleSpread + .34;
                    for (int n = 0; n < 2; n++) {
                        if (n == 1 && hash(id + 37.0) < .28)
                            break;
                        vec2 uv = rotatePoint(delta, angle + float(n) * 1.13);
                        float t = uv.y / wingSpan;
                        float mid = .14 * sin(t * 2.6) - .13 * t;
                        float width = .16 * smoothstep(-.04, .40, t) * (1.0 - smoothstep(.57, 1.0, t));
                        float wing = (1.0 - smoothstep(width, width + .009, abs(uv.x - mid))) *
                                     step(0.0, t) * step(t, 1.0);
                        float veins = .5 + .5 * cos((uv.x - mid) / (width + .022) * 12.0 + t * 9.0);
                        result = mix(result, mix(color3, color4, .36 + .51 * veins), wing * .94);
                        result =
                            mix(result, color3, 1.0 - smoothstep(.047, .078, length(uv * vec2(1.15, 1.0))));
                    }
                }
            }
        """.trimIndent()

        PatternTextureType.OctopusArm -> """
            vec2 q = rotatePoint(p, .33) * vec2(4.5, 5.6) / cupPitch;
            q.x += .18 * sin(q.y * .9);
            q.y += .48 * mod(floor(q.x), 2.0);
            vec2 uv = fract(q) - .5;
            float d = length(uv * vec2(1.15, 1.0));
            float grain = layerNoise(q * 5.0);
            result = mix(color1, color2, .4 + .5 * grain + .07 * skinFolds * sin(q.x * 57.0 + grain * 5.0));
            float rim = exp(-pow((d - .25) / (.046 + .045 * rimHeight), 2.0));
            vec4 lip = mix(color3, color4, .36 + .45 * smoothstep(-.17, .18, -uv.x - uv.y));
            result = mix(result, lip, rim);
            result = mix(result, color1, (1.0 - smoothstep(.10, .15, d)) * .89);
        """.trimIndent()

        PatternTextureType.WardianCase -> """
            vec2 q = p * 23.0;
            vec4 drop = surfaceCell(q, .93);
            float wander = p.x * 5.0 + .21 * sin(p.y * 8.0 + floor(p.x * 5.0) * 1.8) + .14 * noise(p * 7.0);
            float trail = 1.0 - smoothstep(runWidth, runWidth + .045, repeatDistance(wander));
            float haze = layerNoise(p * 9.0);
            result = mix(color1, color2, .20 + .60 * haze);
            result = mix(result, color3, fogAmount * (.52 + .4 * haze) * (1.0 - .81 * trail));
            float r = dropSize * (.55 + .89 * hash(floor(q - drop.zw)));
            float d = length(drop.zw * vec2(1.0, .84));
            float edge = 1.0 - smoothstep(.012, .046, abs(d - r));
            result = mix(result, color1, edge * .54);
            result = mix(result, color4,
                         (1.0 - smoothstep(.025, .067, length(drop.zw + vec2(.45, .5) * r))) * .95 *
                             (1.0 - .6 * trail));
        """.trimIndent()

        PatternTextureType.AntGalleries -> """
            vec2 q = rotatePoint(p, 0.14) * 4.4;
            float nearest = 2.0;
            for (int j = -1; j <= 1; j++) {
                float row = floor(q.y) + float(j);
                float route = row + 0.5 + 0.24 * sin(q.x * 2.9 + row * 1.9) +
                              0.30 * (noise(vec2(q.x * 1.3, row * 5.0)) - 0.5);
                nearest = min(nearest, abs(q.y - route) * 0.79);
                for (int i = -1; i <= 1; i++) {
                    vec2 tile = vec2(floor(q.x) + float(i), row);
                    float x = tile.x + 0.15 + 0.65 * hash(tile + 9.0);
                    float y = row + 0.5 + 0.24 * sin(x * 2.9 + row * 1.9) +
                              0.30 * (noise(vec2(x * 1.3, row * 5.0)) - 0.5);
                    nearest = min(nearest, length((q - vec2(x, y)) * vec2(0.63, 1.0)) -
                                               roomSize * (0.43 + 0.68 * hash(tile + 3.0)));
                    float f = q.y - y;
                    if (f >= 0.0 && f <= 1.10 && hash(tile + 13.0) < junctions) {
                        nearest =
                            min(nearest, abs(q.x - x - 0.18 * sin(f * TAU + tile.x) * sin(f * 3.14159)));
                    }
                }
            }
            float d = nearest + 0.024 * (noise(q * 16.0) - 0.5);
            float soil = 0.40 * layerNoise(p * 57.0) + 0.35 * layerNoise(p * 6.0) + 0.25 * noise(p * 360.0);
            result = mix(color3, color4, soil);
            result = mix(result, color2, 1.0 - smoothstep(tunnelWidth, tunnelWidth + 0.055, d));
            result = mix(result, mix(color1, color2, 0.58 * soil),
                         1.0 - smoothstep(tunnelWidth - 0.02, tunnelWidth + 0.012, d));
        """.trimIndent()

        PatternTextureType.RainShafts -> """
            vec2 q = rotatePoint(p, rainTilt);
            float fog = layerNoise(q * vec2(4.0, 6.0));
            result = mix(color1, color2, fog);
            result = mix(result, color3, veilCover * pow(fog, 2.0) * .79);
            for (int i = 0; i < 3; i++) {
                float depth = 1.0 + float(i) * .56;
                vec2 cell = q * vec2(81.0, 9.0) * depth + float(i) * 13.2;
                vec2 id = floor(cell);
                vec2 uv = fract(cell) - .5;
                float r = hash(id + float(i));
                float line = (1.0 - smoothstep(.018, .082, abs(uv.x - .46 * (r - .5)))) *
                             (1.0 - smoothstep(.03, .5 * rainLength + .05, abs(uv.y)));
                result = mix(result, color4, line * step(1.0 - veilCover, r) * (.32 - .07 * float(i)));
            }
        """.trimIndent()

        PatternTextureType.PitcherPlants -> """
            vec2 q = p * pitcherDensity;
            result = mix(color1, color2, .35 + .44 * layerNoise(q * 10.0));
            float vine = repeatDistance(q.x + .24 * sin(q.y * 2.3) + .17 * q.y);
            result = mix(result, color2, (1.0 - smoothstep(.006, .017, vine)) * .9);
            q.x += .5 * mod(floor(q.y), 2.0);
            vec2 uv = fract(q) - .5;
            vec2 id = floor(q);
            uv = rotatePoint(uv, .35 * (hash(id) - .5));
            float t = (uv.y + .31) / cupDepth;
            float width = .071 + .11 * smoothstep(.03, .58, t) - .072 * smoothstep(.51, 1.0, t);
            float axis = .073 * sin(t * 4.9);
            float mask = (1.0 - smoothstep(width, width + .01, abs(uv.x - axis))) *
                         smoothstep(-.02, .03, t) * (1.0 - smoothstep(.91, 1.00, t));
            float fleck = smoothstep(.58, .78, noise(uv * vec2(49.0, 34.0)));
            result =
                mix(result,
                    mix(color3, color4, .38 + .37 * cos((uv.x - axis) / (width + .01) * 2.0) - .26 * fleck),
                    mask);
            float mouth = length(vec2((uv.x + .001) / .133, (uv.y + .31) / .041));
            result = mix(result, color4, 1.0 - smoothstep(.92, 1.04, mouth));
            result = mix(result, color1, 1.0 - smoothstep(.59, .71, mouth));
            float thread = segment(uv, vec2(-.035, .26), vec2(-.4 * tendrilReach, .49));
            result = mix(result, color2, 1.0 - smoothstep(.004, .014, thread));
        """.trimIndent()

        PatternTextureType.PoleFoldShibori -> """
            vec2 q = rotatePoint(p, .56);
            float wobble = .026 * noise(q * vec2(7.0, 12.0)) + .014 * sin(18.0 * q.y);
            float pleat = (q.x + wobble) / tiePitch + foldBunch * .38 * sin(26.0 * q.y + q.x * 9.0);
            float resist = repeatDistance(pleat);
            float bleed = dyeBleed * (noise(q * vec2(206.0, 32.0)) - .5);
            float white = 1.0 - smoothstep(.046, .223, resist + bleed * .24);
            float tie =
                1.0 - smoothstep(.018, .065, repeatDistance(q.y * 6.0 + .37 * q.x + .11 * noise(q * 19.0)));
            result = mix(color1, color2, .20 + .64 * layerNoise(q * vec2(48.0, 10.0)));
            result = mix(result, color3, white * .92);
            result = mix(result, color4, clamp(white * .79 + tie * .53, 0.0, 1.0));
        """.trimIndent()

        PatternTextureType.PeacockMarbling -> """
            vec2 q = p / combPitch;
            q.y += combTravel * (1.17 * cos(q.x * 3.14159) + .53 * cos((q.x + .47) * TAU)) +
                   .13 * noise(q * vec2(3.0, 1.0));
            q.x += .22 * sin(q.y * 1.8);
            q.y += combTravel * .42 * cos(q.x * TAU + .19 * sin(q.y * 2.0));
            float t = fract(q.y * 2.8 + .13 * sin(q.x * 8.0) + .09 * noise(q * vec2(5.0, 2.0)));
            result = mix(color1, color2, smoothstep(groundShare - .014, groundShare + .014, t));
            result = mix(result, color3, smoothstep(.52, .55, t));
            result = mix(result, color4, smoothstep(.61, .64, t));
            result = mix(result, color1, smoothstep(.88, .91, t));
        """.trimIndent()

        PatternTextureType.LogCabinQuilt -> """
            vec2 q = p * 3.8 + 2.0 * vec2(phaseX, phaseY);
            vec2 id = mod(floor(q), 2.0);
            vec2 uv = fract(q) - .5;
            if (mod(id.x + id.y, 2.0) > .5 && blockTurn > .5)
                uv = vec2(uv.y, -uv.x);
            float radius = max(abs(uv.x), abs(uv.y));
            float strip = radius * stripCount * 2.0;
            float side = abs(uv.x) > abs(uv.y) ? (uv.x > 0.0 ? 0.0 : 2.0) : (uv.y > 0.0 ? 1.0 : 3.0);
            float patch = fract((2.0 * floor(strip) + side + floor(hash(id) * 3.0)) * .267);
            result = palette(patch);
            result = mix(result, color2, 1.0 - smoothstep(.89, 1.0, strip));
            float join = 1.0 - smoothstep(seamWidth * .45, seamWidth, repeatDistance(strip));
            result = mix(result, color1, join * .65);
            result.rgb *= .91 + .11 * noise(vec2(uv.x * 95.0, uv.y * 13.0)) +
                          .06 * sin(uv.x * 657.0) * sin(uv.y * 624.0);
        """.trimIndent()

        PatternTextureType.CoiledRagRug -> """
            vec2 q = p + vec2(.24, -.17);
            q.y *= 1.08;
            float a = safeAngle(q), d = length(q);
            float spiral = d / cordWidth - a / TAU;
            float t = fract(spiral);
            float arch = 1.0 - pow(abs(2.0 * t - 1.0), 2.0);
            float travel = a * d * braidRate;
            float twist = .5 + .5 * cos(TAU * travel + 5.0 * t);
            float patch = hash(vec2(floor((a + 3.14159) * 8.0 + floor(spiral) * 1.4), floor(spiral)));
            result = palette(patch);
            result.rgb *= .56 + (.25 + .22 * channelDepth) * arch + .16 * twist + .07 * noise(p * 315.0) -
                          channelDepth * .24 * (1.0 - smoothstep(.04, .16, repeatDistance(t)));
        """.trimIndent()

        PatternTextureType.ButtonTufting -> """
            vec2 q = vec2(p.x * .90 + p.y * diamondAspect, p.x * .90 - p.y * diamondAspect) * 4.5 +
                     vec2(phaseX, phaseY);
            vec2 uv = fract(q + .5) - .5;
            float puff = pow(max(.0001, abs(sin(q.x * 3.14159) * sin(q.y * 3.14159))), .44);
            float d = length(uv);
            float pinch = cos(10.0 * safeAngle(uv) + d * 24.0) * (1.0 - smoothstep(.02, .25, d));
            result = mix(color1, color2, .2 + tuftDepth * .64 * puff);
            result = mix(result, color3, puff * (.22 + .14 * layerNoise(p * 83.0)) * (.83 + .14 * uv.y));
            result.rgb *= 1.0 - .15 * pinch * tuftDepth;
            result = mix(result, color4, 1.0 - smoothstep(buttonSize * .75, buttonSize, d));
        """.trimIndent()

        PatternTextureType.TornPosterWall -> """
            result = mix(color1, color3, .24);
            for (int i = 0; i < 7; i++) {
                if (float(i) >= posterLayers)
                    break;
                float f = float(i);
                vec2 q = rotatePoint(p + f * .21, .29 * f - .67) / tearSize;
                float cut =
                    fract(q.y + .18 * sin(q.x * 5.4 + f * 3.0) + .11 * noise(q * vec2(21.0, 9.0) + f) +
                          .033 * edgeFiber * noise(q * 112.0) + f * .16);
                float print = 1.0 - smoothstep(.018, .05, repeatDistance(q.x * 4.3 + q.y * .7 + f));
                vec4 sheet = mix(palette(fract(.17 + f * .33)), color1, .21 * print);
                sheet = mix(sheet, color4, smoothstep(.209, .249, cut) * (.58 + .33 * edgeFiber));
                result = mix(result, sheet, 1.0 - smoothstep(.243, .255, cut));
            }
            result.rgb *= .95 + .08 * noise(p * 290.0);
        """.trimIndent()

        PatternTextureType.LatteRosetta -> """
            vec2 q = p * 4.8;
            vec2 fold = vec2(sin(q.y * 1.9 + sin(q.x * 1.2)), cos(q.x * 1.36 - q.y * .7));
            q += milkCurl * 1.35 * fold;
            float stream = q.y + .40 * sin(q.x * 2.6 + .51 * q.y) + .22 * noise(q * 1.6);
            float stripe = repeatDistance(stream * 1.6);
            float foam = 1.0 - smoothstep(milkShare * .13, milkShare * .54, stripe);
            result = mix(mix(color1, color2, .34 + .53 * layerNoise(p * 11.0)),
                         mix(color3, color4, .29 + foam * .67), foam);
            vec4 bubble = surfaceCell(p * 37.0, .90);
            float rim = 1.0 - smoothstep(.019, .052, abs(bubble.x - frothSize * (.5 + noise(p * 9.0))));
            result = mix(result, color4, rim * smoothstep(.29, .68, noise(p * 16.0)) * .65);
        """.trimIndent()

        PatternTextureType.SalmonMarbling -> """
            vec2 q = rotatePoint(p, -0.40);
            float drift = 0.026 * sin(q.x * 9.0 + 1.2) + 0.018 * (layerNoise(q * 6.0) - 0.5);
            float phase = (q.y * 6.9 + 1.15 * length(vec2(q.x + 0.15 + 0.12 * sin(q.y * 4.0), 0.19)) +
                           drift * 5.0 + 0.14 * sin(q.x * 5.0 + q.y * 4.0)) /
                          layerPitch;
            float width = fatWidth * (0.72 + 0.68 * noise(q * 7.0));
            float major = 1.0 - smoothstep(width * 0.6, width * 1.25, repeatDistance(phase));
            float branchA =
                1.0 - smoothstep(0.012, 0.052,
                                 repeatDistance(phase + 0.38 * sin(q.x * 8.0 + q.y * 4.0 + 0.8) + 0.19));
            float branchB =
                1.0 -
                smoothstep(0.009, 0.039, repeatDistance(phase + 0.2 * sin(q.x * 14.0 + q.y * 4.5) - 0.28));
            float level = fatForks + 0.5 * (noise(q * 5.0) - 0.5);
            float veins = max(major, max(branchA * smoothstep(0.34, 0.54, level),
                                         branchB * smoothstep(0.55, 0.79, level)) *
                                         0.87);
            float fibers = 0.5 + 0.5 * cos(TAU * (phase * 13.5 + 1.45 * noise(q * vec2(15.0, 8.0))));
            result = mix(mix(color1, color2, 0.28 + 0.44 * layerNoise(q * 15.0) + 0.23 * fibers),
                         mix(color3, color4, noise(q * vec2(45.0, 87.0))), veins);
        """.trimIndent()

        PatternTextureType.PomegranateCut -> """
            vec2 q = p / arilSize;
            vec2 bent = q * 3.5 + .40 * vec2(noise(q * 9.0), noise(q * 9.0 + 8.0));
            vec4 chamber = surfaceCell(bent, .77);
            vec2 inner = q * 13.0 + .18 * vec2(noise(q * 29.0), noise(q * 29.0 + 20.0));
            vec4 aril = surfaceCell(inner, .82);
            vec4 jewel = mix(color1, color2, .38 + hash(floor(inner - aril.zw)) * .48);
            jewel = mix(jewel, color3, .32 * (1.0 - smoothstep(.18, .54, aril.x)));
            jewel = mix(jewel, color1, .86 * (1.0 - smoothstep(.02, .095, aril.y)));
            float glint = 1.0 - smoothstep(.018, .085, length(aril.zw + vec2(.094, .11)));
            jewel = mix(jewel, color4, glint * arilGloss);
            result = mix(color4, jewel, smoothstep(septaWidth * .6, septaWidth * 1.5, chamber.y));
        """.trimIndent()

        PatternTextureType.LetterpressOverprint -> """
            vec2 q = fract(p + vec2(phaseX, phaseY) + 0.5) - 0.5;
            float tooth = noise(p * 118.0);
            result = mix(color1, color4, 0.045 * tooth);
            vec2 orange = q + vec2(offset, 0.014 + offset * 0.35);
            float roundel = length(orange - vec2(-0.149, 0.087)) - 0.283;
            float block = max(abs(orange.x + 0.145) - 0.174, abs(orange.y + 0.242) - 0.117);
            float orangeMask = coverage(min(roundel, block) + (tooth - 0.5) * 0.008, 0.0);
            vec2 teal = q - vec2(0.105, 0.046);
            float rail = abs(length(teal) - 0.213) - 0.080;
            float bars = abs(fract((teal.y + 0.105 * teal.x) * 10.5) - 0.5) / 10.5 - 0.019;
            float field = max(max(abs(teal.x - 0.062) - 0.181, abs(teal.y - 0.217) - 0.139), bars);
            float blueMask = coverage(min(rail, field) + (tooth - 0.5) * 0.007, 0.0);
            result = mix(result, color2, orangeMask);
            result = mix(result, mix(color3, color4, orangeMask), blueMask);
            result = mix(result, color1, 0.18 * smoothstep(0.79, 0.94, tooth));
        """.trimIndent()

        PatternTextureType.PaperQuilling -> """
            vec2 q = p * 5.0 / coilSize;
            vec4 cell = surfaceCell(q, .93);
            float d = cell.x, a = safeAngle(cell.zw);
            vec2 id = floor(q - cell.zw);
            float phase = d / windPitch - a / TAU + hash(id) * .5;
            float strip = repeatDistance(phase);
            result = mix(color1, color2, .12 + .18 * layerNoise(q * 14.0));
            vec4 pigment = palette(.19 + hash(id) * .62);
            float rim = 1.0 - smoothstep(paperGauge * .45, paperGauge + .035, strip);
            result = mix(result, mix(pigment, color4, .15 + .18 * sin(a - .64)),
                         rim * smoothstep(.015, .085, cell.y));
        """.trimIndent()

        PatternTextureType.GoldLeafTransfer -> """
            vec2 q = rotatePoint(p, -0.14) * 3.4 / leafSize;
            vec2 seams = q + 0.025 * (vec2(noise(q * vec2(4.0, 9.0)), noise(q * vec2(8.0, 4.0))) - 0.5);
            float seam =
                1.0 - smoothstep(0.005, 0.031, min(repeatDistance(seams.x), repeatDistance(seams.y)));
            vec2 bent = q * 3.0 + 0.5 * vec2(noise(q * 2.0), noise(q * 2.0 + 5.0));
            vec4 facets = surfaceCell(bent, 0.83);
            float leaf = hash(floor(bent - facets.zw));
            float fold = 1.0 - smoothstep(0.006, 0.075, facets.y);
            float surface = 0.43 * layerNoise(p * 6.0) + 0.32 * noise(p * 91.0) + 0.25 * noise(p * 210.0) -
                            0.075 * seam;
            float metal = 0.31 + 0.32 * leaf + 0.13 * noise(p * 8.0) +
                          0.28 * sin((q.x * 0.67 + q.y * 0.54) * 3.1 + 1.8 * leaf);
            vec4 foil = mix(color3, color4, clamp(metal, 0.0, 1.0));
            foil.rgb *= 1.0 + foilCreases * (0.17 * seam - 0.30 * fold);
            result = mix(mix(color1, color2, 0.34 + 0.33 * noise(p * 340.0)), foil,
                         smoothstep(gildingWear - 0.032, gildingWear + 0.034, surface));
        """.trimIndent()

        PatternTextureType.CaneWebbing -> """
            vec2 q = rotatePoint(p, 0.017) * 7.0 + 2.0 * vec2(phaseX, phaseY);
            result = mix(color1, color2, 0.24 + 0.37 * layerNoise(p * 21.7));
            float topLayer = -4.0;
            for (int i = 0; i < 6; i++) {
                float fi = float(i);
                float axis = i < 2 ? q.x : (i < 4 ? q.y : (i == 4 ? q.x + q.y + 0.5 : q.x - q.y + 0.5));
                float along = i < 2 ? q.y : (i < 4 ? q.x : (i == 4 ? q.x - q.y : q.x + q.y));
                if (i < 4)
                    axis += mod(fi, 2.0) * 0.26 - 0.13;
                float dist = fract(axis + 0.5) - 0.5;
                float width = reedWidth * (i < 4 ? 0.63 : 1.04);
                float mask =
                    1.0 - smoothstep(width - pixelWidth * 2.8, width + pixelWidth * 2.8, abs(dist));
                float height =
                    0.53 + 0.42 * cos(3.14159 * (floor(axis + 0.5) + floor(along + 0.5) + float(i / 2))) +
                    0.008 * fi;
                if (mask > 0.0 && height > topLayer) {
                    float arch = sqrt(max(0.0, 1.0 - dist * dist / (width * width)));
                    vec4 strand = mix(
                        color3, color4,
                        0.25 + 0.4 * noise(vec2(mod(along, 2.0) * 4.0, mod(floor(axis + 0.5), 2.0) + fi)) +
                            0.24 * arch);
                    strand.rgb *= 1.0 + weaveDepth * (0.36 * (arch - 0.5) - 0.12 * dist / width) +
                                  fiberGrain * 0.11 * cos(dist * 245.0 + noise(p * 105.0));
                    result = mix(result, strand, mask);
                    topLayer = height;
                }
            }
        """.trimIndent()

        PatternTextureType.PleatedHandFan -> """
            vec2 q = rotatePoint(p, .19);
            float crest = abs(fract(q.y * 2.1 + .12) - .5);
            float phase = fract((q.x + .26 * crest) / pleatPitch);
            float surface = phase < facetBias ? phase / facetBias : (1.0 - phase) / (1.0 - facetBias);
            float slope = phase < facetBias ? .78 : .30;
            result = mix(color2, mix(color1, color4, slope), pleatDepth);
            result = mix(result, color3, .21 * surface + .16 * layerNoise(p * 74.0));
            result = mix(result, color1,
                         (1.0 - smoothstep(.008, .06, min(phase, 1.0 - phase))) * pleatDepth * .54);
        """.trimIndent()

        PatternTextureType.TailorTape -> """
            result = mix(color1, color2, .24 + .31 * layerNoise(p * 22.0));
            vec2 qa = rotatePoint(p, -.64), qb = rotatePoint(p, .58);
            float rowA = floor(qa.y * 3.8 + .5), rowB = floor(qb.y * 3.8 + .5);
            bool swap = mod(rowA + rowB, 2.0) > .5;
            for (int i = 0; i < 2; i++) {
                vec2 q = ((i == 0) == swap) ? qa : qb;
                float c = fract(q.y * 3.8 + tapeBend * sin(q.x * 8.0) + .5) - .5;
                float tape = 1.0 - smoothstep(tapeWidth, tapeWidth + .014, abs(c));
                float tick = q.x / tickPitch;
                float tall = 1.0 - step(.5, mod(floor(tick + .5), 5.0));
                float line = (1.0 - smoothstep(.045, .12, repeatDistance(tick))) *
                             smoothstep(.01 - .12 * tall, .04 - .12 * tall, c);
                vec4 cloth = mix(color3, color4, .63 + .27 * noise(q * vec2(75.0, 33.0)));
                result = mix(result, color1,
                             (1.0 - smoothstep(tapeWidth, tapeWidth + .06, abs(c - .027))) * .59);
                result = mix(result, mix(cloth, color1, .89 * line), tape);
            }
        """.trimIndent()

        PatternTextureType.PalmetteFrieze -> """
            vec2 q = fract(p + vec2(phaseX + 0.5, 0.5)) - 0.5;
            result = color1;
            float rail = abs(abs(q.y) - 0.425);
            result = mix(result, color3, coverage(rail, 0.014));
            vec2 v = q - vec2(0.0, 0.262);
            float r = length(v), angle = atan(v.x, -v.y);
            for (int i = -3; i <= 3; i++) {
                float k = float(i);
                float lean = k * 0.325;
                vec2 lobe = rotatePoint(v, -lean);
                float t = clamp(-lobe.y / (0.55 - 0.048 * abs(k)), 0.0, 1.0);
                float width = thickness * pow(max(0.0, sin(t * 3.14159)), 0.76) * (1.0 - 0.08 * abs(k));
                float tip = max(lobe.y, -lobe.y - 0.55 + 0.048 * abs(k));
                float d = max(abs(lobe.x - 0.023 * sin(3.14159 * t) * k) - width, tip);
                vec4 pigment = mod(abs(k), 2.0) > 0.5 ? color4 : color3;
                result = mix(result, pigment, coverage(d, 0.0));
                float vein = abs(lobe.x - 0.018 * sin(t * 3.14159) * k);
                result = mix(result, color2, coverage(vein, 0.006) * coverage(d, -0.009));
            }
            float base = abs(r - 0.109) + max(0.0, abs(angle) - 1.7) * 0.15;
            result = mix(result, color2, coverage(base, 0.014));
            result = mix(result, color3, coverage(r, 0.039));
        """.trimIndent()

        PatternTextureType.Switchyard -> """
            vec2 q = rotatePoint(p, .33);
            vec4 gravel = surfaceCell(q * 57.0 / ballastSize, .94);
            result = mix(color1, color2, .22 + .61 * hash(floor(q * 57.0 / ballastSize - gravel.zw)));
            result.rgb *= .74 + .39 * smoothstep(.0, .13, gravel.y);
            float across = abs(fract(q.x + .5) - .5);
            float tie = repeatDistance(q.y / tiePitch + phaseX);
            result = mix(result, mix(color1, color3, .56 + .22 * noise(q * vec2(15.0, 145.0))),
                         (1.0 - smoothstep(.20, .22, tie)) *
                             (1.0 - smoothstep(railGauge * .5 + .08, railGauge * .5 + .11, across)));
            float rail = abs(across - railGauge * .5);
            result = mix(result, color1, 1.0 - smoothstep(.027, .031, rail));
            result = mix(result, color3, 1.0 - smoothstep(.018, .021, rail));
            result = mix(result, color4, 1.0 - smoothstep(.010, .014, rail));
        """.trimIndent()

        PatternTextureType.SuspensionSpan -> """
            vec2 q = rotatePoint(p, .35);
            float axis = q.x * 5.1;
            float core = fract(axis) - .5;
            float strand =
                q.y * 3.8 / layPitch + strandCount * .5 * asin(clamp(2.0 * core, -.99, .99)) / 3.14159;
            float ridge = .5 + .5 * cos(TAU * strand);
            float wire = .5 + .5 * cos(TAU * (strand * 7.0 + .8 * q.y));
            result = mix(color1, color2, sqrt(max(0.0, 1.0 - 4.0 * core * core)) * .75);
            result = mix(result, color3, ridge * (.37 + .42 * wire));
            result = mix(result, color4,
                         wireShine * pow(ridge, 3.0) * pow(wire, 4.0) * (.4 + .6 * (1.0 - abs(core))));
            result.rgb *= .94 + .09 * noise(q * 140.0);
        """.trimIndent()

        PatternTextureType.RainCrosswalk -> """
            vec2 q = rotatePoint(p, -0.37);
            float stripe = repeatDistance(q.x * 2.65 + phaseX + 0.005 * (noise(p * 120.0) - 0.5));
            float stencil = 1.0 - smoothstep(markWidth * 0.5 - 0.007, markWidth * 0.5 + 0.007, stripe);
            float broad = layerNoise(p * 9.0), grit = noise(p * 220.0), dust = noise(p * 440.0);
            vec4 asphalt = mix(color1, color2, 0.67 * grit + 0.20 * broad + 0.13 * dust);
            asphalt.rgb *= 1.0 - roadWetness * 0.27;
            asphalt = mix(asphalt, color2, roadWetness * smoothstep(0.79, 0.97, grit));
            float chip =
                0.58 * layerNoise(p * 46.0 + 3.0) + 0.42 * noise(p * 183.0 + 4.0) + 0.26 * (broad - 0.5);
            vec4 stones = surfaceCell(p * 6.0 + noise(p * 9.0), 0.8);
            float crack = (1.0 - smoothstep(0.004, 0.025, stones.y)) * smoothstep(0.35, 0.55, broad);
            result =
                mix(asphalt, mix(color3, color4, 0.64 * broad + 0.36 * dust),
                    stencil * smoothstep(paintWear - 0.05, paintWear + 0.05, chip) * (1.0 - 0.84 * crack));
        """.trimIndent()

        PatternTextureType.CofferedRotunda -> """
            vec2 q = p * vec2(3.8, 3.8 / cofferAspect) + 2.0 * vec2(phaseX, phaseY);
            vec2 uv = fract(q) - .5;
            float contour = max(abs(uv.x), abs(uv.y));
            float side = abs(uv.x) > abs(uv.y) ? -.54 * sign(uv.x) : -.83 * sign(uv.y);
            result = mix(color2, color3, .40 + .20 * noise(p * 63.0) + .12 * hash(mod(floor(q), 2.0)));
            for (int i = 0; i < 3; i++) {
                float edge = .45 - .095 * float(i);
                float bevel = 1.0 - smoothstep(.53 * rimGauge, 1.18 * rimGauge, abs(contour - edge));
                result = mix(result, side > 0.0 ? color4 : color1, cofferDepth * abs(side) * bevel);
                result.rgb *= 1.0 - cofferDepth * .11 * (1.0 - step(edge, contour));
            }
        """.trimIndent()

        PatternTextureType.StationCanopy -> """
            vec2 q = rotatePoint(p, .70) / meshPitch;
            q += .023 * vec2(noise(q * .7), noise(q * .72 + 7.0));
            q += vec2(phaseX, phaseY);
            float wire = min(repeatDistance(q.x), repeatDistance(q.y));
            float clouds = layerNoise(p * 7.0);
            result = mix(color1, color2, .38 + .6 * clouds);
            result = mix(result, color3, glassHaze * (.2 + .65 * clouds));
            float shadow = 1.0 - smoothstep(wireWidth, wireWidth + .055, wire);
            result = mix(result, color1, shadow * (.77 - .20 * glassHaze));
            float glint =
                1.0 - smoothstep(.006, .023, min(repeatDistance(q.x - .018), repeatDistance(q.y - .018)));
            result = mix(result, color4, .52 * glint);
        """.trimIndent()

        PatternTextureType.PendulumCase -> """
            vec2 q = rotatePoint(p, .29);
            float row = floor(q.x / .23 + .5);
            q.x -= row * .23 + .015 * sin(q.y * 6.0 + row);
            result = mix(color1, color2, .13 + .35 * layerNoise(p * 17.0));
            float idx = floor(q.y / linkLength);
            for (int i = -1; i <= 1; i++) {
                float j = idx + float(i);
                float end = mod(row + j, 2.0);
                float scale = mix(.044, .014, end);
                vec2 local = vec2(q.x, (q.y - j * linkLength));
                local.y = sign(local.y) * max(abs(local.y) - linkLength * .39, 0.0);
                vec2 edge = local / vec2(scale, linkLength * .34);
                float r = length(edge) - 1.0;
                float w = linkGauge + end * .24;
                float mask = 1.0 - smoothstep(w, w + .07, abs(r));
                vec4 metal = mix(color2, color3, .35 + .45 * (1.0 - smoothstep(0.0, w, abs(r))));
                float light = .45 + .52 * sin(safeAngle(edge) + 2.3);
                metal = mix(metal, color4, brassShine * light * (1.0 - smoothstep(.05, w, abs(r))));
                result = mix(result, metal, mask);
            }
        """.trimIndent()

        PatternTextureType.ScrewBookpress -> """
            vec2 q = rotatePoint(p, .68) / toolPitch + vec2(phaseX, 2.0 * phaseY);
            q.x += .5 * mod(floor(q.y), 2.0);
            vec2 uv = fract(q) - .5;
            float eye = abs(length(uv * vec2(1.05, 1.90)) - .35);
            float tip = abs(uv.y + .10 * sin(uv.x * 8.3));
            float mark =
                min(eye, min(max(tip, abs(uv.x) - .20), max(abs(abs(uv.y) - .43), abs(uv.x) * .51)));
            float edge = 1.0 - smoothstep(.009, .034, mark);
            float grain = layerNoise(p * 95.0);
            result = mix(color1, color2, .38 + hideGrain * .60 * grain);
            result = mix(result, color3, stampDepth * .82 * (1.0 - smoothstep(.039, .079, mark)));
            result = mix(result, color1, edge * stampDepth);
            result = mix(result, color4, stampDepth * .25 * edge * smoothstep(-.05, .26, -uv.y));
        """.trimIndent()

        PatternTextureType.TheatreWings -> """
            vec2 q = vec2(p.x / swagPitch + phaseX, p.y / swagPitch);
            float arc = pow(abs(sin(q.x * 3.14159)), .72);
            float wave = q.y - swagDrop * 2.0 * arc;
            float tier = fract(wave + .43);
            float pleat = .5 + .5 * cos(TAU * (tier * drapeFolds * .65 + .42 * cos(q.x * TAU)));
            result = mix(color1, color2, .18 + .49 * arc + .28 * pleat);
            result = mix(result, color3, pow(pleat, 2.0) * arc * .54);
            result = mix(result, color4, (1.0 - smoothstep(.012, .044, tier)) * .75);
            result.rgb *= .96 + .08 * noise(p * vec2(80.0, 230.0));
        """.trimIndent()

        PatternTextureType.HornGramophone -> """
            vec2 q = rotatePoint(p, -.26);
            vec4 hammer = surfaceCell(q * 24.0, .92);
            float t = hash(floor(q * 24.0 - hammer.zw));
            float grain = layerNoise(p * 18.0);
            result = mix(color2, color3, .28 + .31 * grain + hammerMarks * (.28 * t + .13 * hammer.z));
            result = mix(result, color1, brassPatina * smoothstep(.48, .72, grain));
            float x = fract(q.x * 2.45 + .5) - .5;
            result = mix(result, color1, .70 * (1.0 - smoothstep(.004, .01, abs(x - .032))));
            vec2 rivet = vec2(x / 2.45, fract(q.y / rivetPitch + .5) * rivetPitch - .5 * rivetPitch);
            float d = length(rivet) / .018;
            result = mix(result, color1, (1.0 - smoothstep(.94, 1.16, d)) * .73);
            result = mix(result, color3, 1.0 - smoothstep(.73, .90, d));
            result = mix(result, color4, 1.0 - smoothstep(.25, .67, length(rivet / .018 + vec2(.22, .24))));
        """.trimIndent()

        PatternTextureType.CanalLock -> """
            vec2 q = rotatePoint(p, .11);
            float phase = fract(q.x / pilePitch);
            float profile = abs(phase - .5);
            float wall = smoothstep(.16, .27, profile);
            float bevel = 1.0 - smoothstep(.01, .062, abs(profile - .22));
            result = mix(color1, color3, .27 + .34 * wall + .16 * layerNoise(p * 49.0));
            result = mix(result, phase > .5 ? color1 : color3, bevel * fluteDepth);
            float oxide = layerNoise(q * vec2(129.0, 21.0 / (runLength + .16)));
            result = mix(result, color4, smoothstep(.56, .76, oxide) * (.44 + .48 * bevel));
            result = mix(result, color2, .09);
        """.trimIndent()

        PatternTextureType.FunicularIncline -> """
            vec2 q = rotatePoint(p, .38);
            float y = fract(q.y * 3.25) - .5;
            float phase = fract(q.x / gearPitch + phaseX) - .5;
            float top = mix(.07, .23, 1.0 - smoothstep(.10 + .18 * toothForm, .43, abs(phase)));
            float steel = 1.0 - smoothstep(top - .008, top + .009, abs(y));
            float shine = 1.0 - smoothstep(.008, .045, abs(abs(y) - top + .022));
            float grime = layerNoise(p * vec2(49.0, 130.0));
            result = mix(color1, color2, .20 + .42 * grime);
            vec4 rack = mix(color2, color3, .26 + .57 * grime);
            rack = mix(rack, color4, shine * .78);
            rack = mix(rack, color1, greaseDepth * (1.0 - smoothstep(.05, .16, abs(y))) * grime);
            result = mix(result, rack, steel);
        """.trimIndent()

        PatternTextureType.CinemaMarquee -> """
            vec2 q = p * vec2(1.0, .89) / lampPitch + vec2(phaseX, phaseY);
            vec2 uv = fract(q + .5) - .5;
            float d = length(uv), power = 1.0 - lampVariance * noise(p / lampPitch * .89 + 7.0);
            result = mix(color1, color2, layerNoise(p * 8.0));
            result = mix(result, color3, exp(-d * d / (haloRadius * haloRadius)) * .61 * power);
            result = mix(result, color1, 1.0 - smoothstep(.185, .211, d));
            result = mix(result, color2, 1.0 - smoothstep(.157, .177, d));
            result = mix(result, color4, (1.0 - smoothstep(.115, .15, d)) * (.41 + .58 * power));
            result = mix(result, color4, 1.0 - smoothstep(.016, .049, length(uv + vec2(.048, .057))));
        """.trimIndent()

        PatternTextureType.SolarProminences -> """
            vec2 q = rotatePoint(p, -.48) * loopPitch;
            result = mix(color1, color2, layerNoise(q * 4.0) * .76);
            float light = 0.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    vec2 uv = q - id - .18 - .61 * hash2(id);
                    uv /= vec2(.36 + .40 * hash(id + 8.0), plumeHeight * (.55 + .5 * hash(id + 6.0)));
                    float a = safeAngle(uv), d = length(uv) + .028 * sin(a * 5.0 + hash(id) * TAU);
                    float arc =
                        exp(-pow((d - 1.0) / (strandWidth * 1.7), 2.0)) * smoothstep(-.04, .24, uv.y);
                    float fil = pow(.5 + .5 * cos(d * 102.0 + a * 4.0), 3.0);
                    light += arc * (.19 + .77 * fil) * (.36 + .7 * noise(q * 11.0));
                }
            result =
                mix(result, mix(color3, color4, clamp(light, 0.0, 1.0)), clamp(light * 1.62, 0.0, 1.0));
        """.trimIndent()

        PatternTextureType.StellarBowShock -> """
            vec2 q = rotatePoint(p, -.54);
            float turb = layerNoise(q * 5.0);
            float flow = q.x / frontPitch + shockBend * cos((q.y + .18 * sin(q.x * 5.0)) * 6.0) +
                         .14 * layerNoise(q * 18.0);
            float d = repeatDistance(flow), dust = layerNoise(q * vec2(31.0, 16.0));
            result = mix(color1, color2, turb * .86);
            result = mix(result, color3, exp(-d * 5.0) * .75);
            result = mix(result, color4, exp(-d * 11.0) * (.23 + dustDensity * dust) * .96);
        """.trimIndent()

        PatternTextureType.HourglassNebula -> """
            vec2 q = p * 2.7 / cavitySize;
            vec2 drift = vec2(layerNoise(q * 2.0), layerNoise(q * 2.0 + 13.0));
            vec4 cell = surfaceCell(q + .56 * drift, .86);
            float fine = layerNoise(q * 32.0);
            float shell = exp(-cell.y / (shellWall + .014));
            float threads = .5 + .5 * cos(cell.y * 108.0 + 4.0 * noise(q * 19.0) + cell.z * 2.0);
            result = mix(color1, color2, .15 + .48 * drift.x + .28 * shell);
            result = mix(result, color3, shell * (.25 + .5 * fine));
            result = mix(result, color4, shell * gasFilaments * pow(threads, 3.0) * (.33 + .49 * fine));
            float star = hash(floor(p * 172.0));
            result = mix(result, color4, step(.9985, star) * .70);
        """.trimIndent()

        PatternTextureType.RocheLobes -> """
            vec2 q = p * 2.7;
            float potential = 0.0, star = 2.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    vec2 uv = q - id - .5 - .19 * (hash2(id) - .5);
                    uv = rotatePoint(uv, (hash(id + 15.0) - .5) * 1.8);
                    float r1 = length(uv - vec2(fociGap * .5, 0.0)),
                          r2 = length(uv + vec2(fociGap * .5, 0.0));
                    star = min(star, min(r1, r2));
                    potential += exp(-6.0 * r1) + massRatio * exp(-6.0 * r2);
                }
            result = mix(color1, color2, min(.88, potential * .77));
            result =
                mix(result, color3, 1.0 - smoothstep(.03, .12, repeatDistance(potential / contourPitch)));
            result = mix(result, color4, 1.0 - smoothstep(.020, .045, star));
        """.trimIndent()

        PatternTextureType.BasaltColonnade -> """
            vec2 q = p * 7.8 / columnSize + .11 * vec2(noise(p * 5.0), noise(p * 6.0 + 7.0));
            vec2 tile = floor(q / vec2(1.0, .866));
            float near = 9.0, second = 9.0, h = 0.0;
            vec2 local = vec2(0.0);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = tile + vec2(float(i), float(j));
                    float level = hash(id);
                    vec2 center = vec2(id.x + .5 * mod(id.y, 2.0), id.y * .866) + .15 * hash2(id + 3.0);
                    center.y -= level * heightSpread * .24;
                    vec2 delta = q - center;
                    float dist = length(delta);
                    if (dist < near) {
                        second = near;
                        near = dist;
                        local = delta;
                        h = level;
                    } else {
                        second = min(second, dist);
                    }
                }
            float gap = second - near;
            float face =
                (1.0 - smoothstep(.05 + .30 * h * heightSpread, .08 + .30 * h * heightSpread, gap)) *
                smoothstep(-.07, .19, local.y);
            result = mix(color2, color3, .11 + .55 * h + .29 * layerNoise(q * 13.0));
            result = mix(result, color1, face * (.45 + .32 * step(0.0, local.x)));
            result = mix(result, color1, 1.0 - smoothstep(columnGap * .46, columnGap * 1.26, gap));
            result = mix(result, color4, .07 * noise(q * 28.0));
        """.trimIndent()

        PatternTextureType.FaultedStrata -> """
            vec2 q = rotatePoint(p, -0.23);
            float plane = (q.x * 2.4 + 0.42 * q.y + 0.06 * (layerNoise(q * 4.0) - 0.5)) / faultPitch + 0.22;
            float block = floor(plane);
            float v = q.y + faultSlip * (0.74 * block + 0.42 * sin(6.2 * block + 1.0));
            v += 0.035 * sin(5.7 * q.x + 3.0 * q.y) + 0.035 * (layerNoise(q * 4.0) - 0.5);
            float bed = v * bedCount + 0.14 * sin(v * 25.0) + 0.043 * (layerNoise(q * 18.0) - 0.5);
            float material = hash(vec2(floor(bed), 6.0));
            result = toneRamp(0.15 + material * 0.81);
            float seam = 1.0 - smoothstep(0.012, 0.12, repeatDistance(bed));
            result = mix(result, color1, 0.63 * seam);
            result = mix(result, color1, (1.0 - smoothstep(0.002, 0.01, repeatDistance(plane))) * 0.81);
            result.rgb *= 0.80 + 0.34 * layerNoise(vec2(q.x * 55.0, v * 180.0)) + 0.06 * sin(bed * 30.0);
        """.trimIndent()

        PatternTextureType.GarnetSchist -> """
            vec2 s = rotatePoint(p, 0.52);
            float fold = noise(vec2(s.x * 4.1, s.y * 3.1));
            float lamina = s.y + 0.077 * fold + 0.023 * sin(s.x * 12.0);
            float grit = noise(vec2(s.x * 32.0, lamina * 185.0));
            float grain = noise(vec2(s.x * 9.0, lamina * 73.0));
            result = mix(color1, color2, 0.20 + 0.69 * grain);
            result = mix(result, color4, smoothstep(0.61, 0.91, grit) * (0.44 + 0.5 * grain));
            vec2 grid = p * 5.3, tile = floor(grid);
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 id = tile + vec2(float(x), float(y));
                    float site = hash(id + 4.2);
                    if (site < 0.38) continue;
                    vec2 u = grid - (id + 0.16 + 0.69 * hash2(id));
                    u = rotatePoint(u, hash(id + 8.0) * TAU);
                    float r = (0.10 + site * 0.17) * radius;
                    float a = safeAngle(u);
                    float facets = floor(a / TAU * 6.0 + 0.5);
                    float hex = length(u) * cos(a - facets * TAU / 6.0) - r;
                    float inner = length(u - vec2(-r * 0.10, r * 0.12));
                    vec4 jewel = mix(color3, color1, 0.12 + 0.29 * (0.5 + 0.5 * cos(facets * TAU / 6.0)));
                    jewel = mix(jewel, color4, coverage(inner, r * 0.53) * 0.17 + 0.11 * grit);
                    result = mix(result, color1, coverage(hex / 5.3, 0.004));
                    result = mix(result, jewel, coverage(hex / 5.3, 0.0));
                    result = mix(result, color4, coverage(abs(hex) / 5.3, 0.0014) * (0.35 + 0.25 * cos(a)));
                }
        """.trimIndent()

        PatternTextureType.OoliticLimestone -> """
            vec2 grid = p * 9.0, tile = floor(grid);
            float matrix = noise(p * 90.0);
            result = mix(color1, color2, 0.42 + 0.42 * matrix);
            float nearest = 10.0;
            vec2 core = vec2(0.0), chosen = tile;
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 id = tile + vec2(float(x), float(y));
                    vec2 q = grid - (id + 0.16 + 0.68 * hash2(id));
                    q = rotatePoint(q, TAU * hash(id + 4.5));
                    q.x *= 0.77 + 0.36 * hash(id + 1.3);
                    float d = length(q) / (0.37 + 0.16 * hash(id + 6.2));
                    if (d < nearest) {
                        nearest = d;
                        core = q;
                        chosen = id;
                    }
                }
            float angle = safeAngle(core), phase = hash(chosen + 2.8);
            float cortex = nearest + 0.032 * sin(angle * 3.0 + phase * TAU) + 0.018 * (matrix - 0.5);
            float lamella = 0.5 + 0.5 * cos(TAU * (cortex * count + phase * 0.60));
            vec4 ooid = mix(color2, color3, 0.18 + 0.66 * lamella);
            ooid = mix(ooid, color4, (1.0 - smoothstep(0.13, 0.29, nearest)) * (0.50 + 0.43 * phase));
            result = mix(result, color1, coverage((nearest - 1.0) / 18.0, 0.0018));
            result = mix(result, ooid, coverage((nearest - 1.0) / 18.0, -0.001));
        """.trimIndent()

        PatternTextureType.TrilobiteImpression -> """
            vec2 q = p * fossilCount;
            vec2 id = floor(q);
            vec2 uv = rotatePoint(fract(q) - .5 - .16 * (hash2(id) - .5), hash(id + 6.0) * TAU);
            float shell = length(uv / vec2(.239, .428));
            float stone = layerNoise(p * 45.0);
            float shape =
                (1.0 - smoothstep(.92, 1.02, shell)) *
                smoothstep(.65 - .58 * preservation, .91 - .54 * preservation, layerNoise(q * 7.0));
            float rib = .5 + .5 * cos((uv.y + .045 + .62 * max(0.0, abs(uv.x) - .059)) * 98.0);
            float axis = 1.0 - smoothstep(.028, .078, abs(uv.x));
            result = mix(color1, color2, .32 + .55 * stone);
            vec4 imprint = mix(color2, color4, .19 + fossilRelief * (.31 * rib + .20 * axis) + .14 * stone);
            imprint =
                mix(imprint, color3, 1.0 - smoothstep(.084, .12, length(vec2(uv.x * .9, uv.y + .28))));
            result = mix(result, imprint, shape);
        """.trimIndent()

        PatternTextureType.DoubleSlit -> """
            vec2 q = p * vec2(3.4, 2.8);
            float wave = 0.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 uv = fract(q) - .5 - vec2(float(i), float(j));
                    float a = length(uv - vec2(sourceGap * .5, 0.0)),
                          b = length(uv + vec2(sourceGap * .5, 0.0));
                    wave += (cos(a * TAU / waveLength) + cos(b * TAU / waveLength)) *
                            exp(-(a + b) * (.64 + attenuation * 1.14));
                }
            result = toneRamp(clamp(.08 + .65 * abs(wave), 0.0, 1.0));
        """.trimIndent()

        PatternTextureType.LensCrossSection -> """
            vec2 q = p * 2.4;
            vec2 shift = .32 * gapSlope * vec2(noise(q * 1.7), noise(q * 1.7 + 4.0));
            vec4 cell = surfaceCell(q + shift, .72);
            float phase = fringeRate * (cell.x * cell.x + .15 * gapSlope * layerNoise(q * 2.8));
            float base = fract(phase);
            vec4 hue = palette(base);
            float fringe = .5 + .5 * cos(TAU * phase);
            result = mix(color1, hue, smoothstep(.03, 1.02 - spectrumWidth * .42, fringe) * .91);
        """.trimIndent()

        PatternTextureType.IrisDiaphragm -> """
            vec2 q = p * 2.8 + vec2(phaseX, phaseY);
            vec2 uv = fract(q + .5) - .5;
            float radius = length(uv), angle = safeAngle(uv);
            float blade = fract(angle / TAU * bladeCount + radius * 3.3);
            result = mix(color2, color3, (.21 + .66 * blade) * (.78 + .22 * noise(p * 168.0)));
            result = mix(result, color1, bladeDepth * (1.0 - smoothstep(.018, .14, blade)));
            result = mix(result, color4, (1.0 - smoothstep(.00, .04, 1.0 - blade)) * .63 * bladeDepth);
            result = mix(result, color1, 1.0 - smoothstep(aperture, aperture + .01, radius));
        """.trimIndent()

        PatternTextureType.EmissionSpectrum -> """
            float q = p.x * lineCount;
            result = color1;
            for (int i = -1; i <= 1; i++) {
                float bin = floor(q) + float(i);
                float d = q - bin - .12 - .70 * hash(vec2(bin, 5.0));
                float pair = hash(vec2(bin, 16.0));
                if (pair < doublets)
                    d = min(abs(d), abs(d - .16 - .11 * pair));
                float strength = .37 + .63 * hash(vec2(bin, 17.0));
                vec4 spectral = palette(.25 + .70 * fract(bin * .034));
                float core = exp(-pow(d / lineWidth, 2.0));
                float halo = exp(-abs(d) / (lineWidth * 2.9));
                result = mix(result, spectral, (.87 * core + .13 * halo) * strength);
            }
        """.trimIndent()

        PatternTextureType.SchlierenPlume -> """
            vec2 q = p * 5.0 / flowWidth;
            float dd = .015;
            float left = q.x + .81 * flowCurl * sin(q.y * 1.85 + .6 * sin(q.x * 2.0)) +
                         .50 * layerNoise(q * vec2(1.2, .8));
            vec2 s = q + vec2(dd, 0.0);
            float right = s.x + .81 * flowCurl * sin(s.y * 1.85 + .6 * sin(s.x * 2.0)) +
                          .50 * layerNoise(s * vec2(1.2, .8));
            float fld = sin(TAU * left + .51 * sin(q.y * 5.0 + left)) + .45 * cos(left * 9.0 + q.y * .45);
            float nextField =
                sin(TAU * right + .51 * sin(q.y * 5.0 + right)) + .45 * cos(right * 9.0 + q.y * .45);
            result = toneRamp(clamp(.51 + edgeContrast * (nextField - fld) * 3.1, 0.0, 1.0));
        """.trimIndent()

        PatternTextureType.HandRadiograph -> """
            vec2 q = rotatePoint(p, 0.37) * 13.0 * vec2(poreAspect, 1.0 / pow(poreAspect, 0.65)) / poreSize;
            vec2 warped = q + 1.40 * vec2(noise(q * 0.54), noise(q * 0.54 + 9.0));
            vec4 cell = surfaceCell(warped, 0.82);
            float grain = noise(q * 2.6);
            float wall = cell.y - 0.085 * grain;
            float porous = 0.40 + 0.60 * smoothstep(0.20, 0.44, noise(q * 0.78 + 42.0));
            float strut = (1.0 - smoothstep(strutWidth - 0.045, strutWidth + 0.060, wall)) * porous;
            vec4 cavity = mix(color1, color2, 0.69 * layerNoise(q * 2.2) + 0.20 * strut);
            vec4 bone =
                mix(color3, color4, 0.23 + 0.49 * grain + 0.26 * (1.0 - smoothstep(0.0, strutWidth, wall)));
            result = mix(cavity, bone, smoothstep(0.20, 0.82, strut));
            result.rgb *= 0.95 + 0.10 * noise(q * 20.0);
        """.trimIndent()

        PatternTextureType.LogisticBifurcation -> """
            vec2 q = p * vec2(2.2, 2.6);
            q.x += .38 * mod(floor(q.y), 2.0);
            vec2 uv = fract(q);
            float rate = 3.05 + .94 * uv.x;
            float target = .07 + .88 * uv.y;
            float ink = 0.0;
            for (int i = 0; i < 14; i++) {
                if (float(i) >= sampleDensity)
                    break;
                float value = .13 + .7 * hash(vec2(float(i), floor(q.y) + 4.0));
                for (int j = 0; j < 28; j++) {
                    if (float(j) >= mapIterations)
                        break;
                    value = rate * value * (1.0 - value);
                    if (float(j) > mapIterations - 5.0)
                        ink = max(ink, 1.0 - smoothstep(.001, .009, abs(value - target)));
                }
            }
            result = mix(color1, color2, layerNoise(q * 5.0) * .47);
            result = mix(result, mix(color3, color4, uv.x), ink * (.25 + .75 * branchContrast));
        """.trimIndent()

        PatternTextureType.SunstoneFeldspar -> """
            vec2 q = rotatePoint(p, -0.39);
            float veins = layerNoise(q * vec2(4.0, 13.0));
            result = toneRamp(clamp(0.26 + 0.79 * veins + 0.13 * sin(q.y * 48.0 + 5.0 * veins), 0.0, 1.0));
            for (int i = 0; i < 2; i++) {
                float f = i == 0 ? 32.0 : 72.0;
                vec2 grid = q * f, tile = floor(grid),
                     local = fract(grid) - 0.5 - 0.32 * (hash2(tile) - 0.5);
                local =
                    rotatePoint(local, 0.6 + (1.0 - plateletAlignment) * TAU * (hash(tile + 88.0) - 0.5));
                float mask = coverage(ellipseDistance(local, vec2(0.34, 0.074)) / f, 0.0) *
                             step(hash(tile + 71.1), plateletDensity);
                result = mix(result, mix(color1, color4, smoothstep(0.19, 0.53, hash(tile + 4.1))), mask);
            }
            result.rgb *= 0.93 + 0.14 * noise(p * 207.0);
        """.trimIndent()

        PatternTextureType.RingPunchedSuede -> """
            vec2 g = p / broguePitch + vec2(phaseX, phaseY);
            vec2 q = fract(g) - 0.5;
            float theta = safeAngle(q);
            float arc = theta / TAU * 16.0;
            vec2 nearest = vec2(cos(floor(arc + 0.5) * TAU / 16.0), sin(floor(arc + 0.5) * TAU / 16.0));
            float polar = length(q - nearest * (0.26 + 0.063 * cos(floor(arc + 0.5) * TAU / 4.0)));
            float holes = min(polar, length(q) * 0.72) * broguePitch;
            float suede = layerNoise(p * vec2(91.0, 159.0));
            result = mix(color2, color3, 0.43 + nap * (suede - 0.33));
            result = mix(result, color4, reliefBand(holes - holeDiameter * 0.52, 0.0016) * 0.80);
            result = mix(result, color1, coverage(holes, holeDiameter * 0.46));
        """.trimIndent()

        PatternTextureType.SpindleMoulding -> """
            vec2 g = vec2(p.x / beadPitch + phaseX, p.y * 4.2);
            float x = fract(g.x) - 0.5, y = fract(g.y) - 0.5;
            float cap = sqrt(max(0.0, 1.0 - pow(abs(x) / 0.37, 2.0)));
            float collar = 1.0 - smoothstep(collarWidth * 0.3, collarWidth, abs(abs(x) - 0.43));
            float radius = 0.13 + beadSwell * 0.22 * cap + 0.093 * collar;
            float side = y / radius, surface = sqrt(max(0.0, 1.0 - side * side));
            float wood = layerNoise(vec2(p.x * 13.0, p.y * 79.0));
            result = mix(color1, color2, wood * 0.68);
            vec4 carving =
                mix(color2, color4, clamp(0.14 + surface * 0.49 - side * 0.39 + 0.23 * wood, 0.0, 1.0));
            carving = mix(carving, color3, collar * 0.71);
            carving.rgb *= 0.78 + 0.18 * cap + 0.18 * smoothstep(0.20, 0.79, cos(x * 18.0 - 0.62));
            result = mix(result, carving, coverage((abs(y) - radius) / 4.2, 0.0));
        """.trimIndent()

        PatternTextureType.LenoGauze -> """
            vec2 g = p / vec2(weftPitch * 0.85, weftPitch) + vec2(phaseX, 2.0 * phaseY);
            float cross = sin(g.y * 3.14159), bend = (0.06 + 0.22 * warpTwist) * cross;
            float onTop = step(0.0, cos(g.y * 3.14159));
            float first = reliefBand(repeatDistance(g.x - bend) * weftPitch * 0.85, yarnWidth);
            float second = reliefBand(repeatDistance(g.x + bend) * weftPitch * 0.85, yarnWidth * 0.92);
            float weft = reliefBand(repeatDistance(g.y) * weftPitch, yarnWidth * 1.2);
            result = mix(color1, color2, 0.17 + 0.09 * layerNoise(p * 24.0));
            result = mix(result, color3, first);
            result = mix(result, color4, second);
            result = mix(result, color2, weft);
            result = mix(result, mix(color3, color4, onTop), mix(first, second, onTop) * weft);
        """.trimIndent()

        PatternTextureType.SoumakBraid -> """
            vec2 g = vec2(p.x / wrapPitch, p.y * 11.0);
            float row = floor(g.y), dir = mod(row, 2.0) * 2.0 - 1.0;
            float y = fract(g.y) - 0.5;
            float x = fract(g.x + dir * wrapLean * y + mod(row, 2.0) * 0.5) - 0.5;
            float cord = abs(x) / 0.45 + pow(abs(y) * 2.04, 4.0);
            float hairs = layerNoise(vec2(g.x * 5.0 + dir * g.y * 8.0, g.y * 22.0));
            result = mix(color1, color2, 0.16 + 0.20 * hairs);
            vec4 wool = mix(color2, mix(color3, color4, step(2.0, mod(row, 4.0))),
                            0.32 + 0.43 * (1.0 - abs(x) * 1.7));
            wool.rgb *= 0.88 + 0.22 * woolHair * hairs;
            result = mix(result, wool, coverage((cord - 1.0) * wrapPitch, 0.0));
        """.trimIndent()

        PatternTextureType.CouchingLoops -> """
            vec2 g = p / loopPitch + vec2(phaseX, phaseY);
            float bend = 0.25 * sin(g.x * TAU);
            float curve = fract(g.y - bend + 0.5) - 0.5;
            float gauge = abs(curve) * loopPitch / sqrt(1.0 + 2.46 * pow(abs(cos(g.x * TAU)), 2.0));
            float glint = 0.5 + 0.5 * cos(p.x * 410.0 + 6.0 * curve);
            result = mix(color1, color2, 0.18 * layerNoise(p * 105.0));
            vec4 cord = mix(color3, color4, 0.20 + 0.53 * glint + 0.19 * (1.0 - gauge / cordGauge));
            result = mix(result, cord, reliefBand(gauge, cordGauge));
            float tie = reliefBand(repeatDistance(p.x / tieSpacing + curve * 0.9) * tieSpacing, 0.0016);
            result = mix(result, color2, tie * reliefBand(gauge, cordGauge * 1.26));
        """.trimIndent()

        PatternTextureType.CrazyQuiltSeams -> """
            vec2 g = rotatePoint(p, 0.19) / patchSize;
            vec3 cell = cellSample(g);
            vec4 patch = toneRamp(0.09 + 0.73 * cell.z);
            float weave = 0.5 + 0.5 * cos(p.x * 513.0 + cell.z * 5.0) * cos(p.y * 607.0);
            result = mix(patch, color1, 0.06 + 0.09 * weave);
            float t = p.x * stitchDensity + p.y * stitchDensity * 0.6;
            float stitch = abs(cell.y - (0.035 + seamFeather * (1.0 - 2.0 * repeatDistance(t))));
            result = mix(result, color1, reliefBand(cell.y * patchSize, 0.0018));
            result = mix(result, color4,
                         reliefBand(stitch * patchSize, 0.00165) * step(cell.y, seamFeather + 0.055));
        """.trimIndent()

        PatternTextureType.PassementerieFrogs -> """
            vec2 g = p / knotPitch + vec2(phaseX, phaseY), q = fract(g) - 0.5;
            float side = sign(q.x), x = abs(q.x);
            vec2 bend = vec2(x - loopSpan * 0.51, q.y);
            float d = abs(ellipseDistance(rotatePoint(bend, 0.31 * side), vec2(loopSpan * 0.63, 0.158)));
            d = min(d, abs(ellipseDistance(rotatePoint(bend, -0.74 * side), vec2(loopSpan * 0.50, 0.096))));
            float yarn = pow(0.5 + 0.5 * cos((x + q.y * 0.6) * 151.0), 2.0);
            d = min(d, abs(ellipseDistance(vec2(x - 0.045, q.y + 0.045), vec2(0.058, 0.083))));
            result = mix(color1, color2, 0.16 + 0.17 * layerNoise(p * 48.0));
            result =
                mix(result, mix(color3, color4, 0.12 + 0.70 * yarn), reliefBand(d * knotPitch, braidGauge));
            float button = length(q * vec2(1.2, 0.90));
            result = mix(result, color3, coverage(button * knotPitch, 0.043 * knotPitch));
        """.trimIndent()

        PatternTextureType.RyaPile -> """
            vec2 g = p * vec2(13.0, 9.0);
            result = mix(color1, color2, 0.24 * noise(p * 102.0));
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(g) + vec2(float(i), float(j)),
                         root = (id + 0.5 + tuftScatter * (hash2(id) - 0.5) * 0.68) / vec2(13.0, 9.0);
                    vec2 v = p - root;
                    float h = hash(id + 8.0);
                    for (int k = 0; k < 6; k++) {
                        float s = float(k) / 5.0 - 0.5;
                        float yy = v.y / pileLength;
                        float dx = v.x - pileLength * (s * 0.76 + 0.40 * (h - 0.5)) * yy -
                                   fibreCurl * 0.040 *
                                       (sin(yy * 5.4 + h * 5.0 + s * 0.8) - sin(h * 5.0 + s * 0.8));
                        float endY = 0.56 + 0.35 * h + 0.17 * hash(id + float(k) * 3.1);
                        float m = reliefBand(dx, 0.0026) * smoothstep(-0.04, 0.05, yy) *
                                  (1.0 - smoothstep(endY - 0.08, endY, yy));
                        result = mix(result, toneRamp(0.20 + 0.49 * h + 0.18 * s + 0.10 * yy), m);
                    }
                }
        """.trimIndent()

        PatternTextureType.OttomanCord -> """
            float g = p.y / cordGauge + phaseX, t = fract(g) - 0.5;
            float cross = cos(TAU * p.x * bindingCount / cordGauge);
            float height = sqrt(max(0.0, 1.0 - pow(abs(t) * 2.0, 2.0)));
            float tie = step(0.83, cross) * smoothstep(0.15, 0.48, abs(t));
            result = mix(color1, color2, 0.33 + 0.43 * height);
            result = mix(result, color3, ribLoft * (0.27 + 0.50 * height - 0.46 * t));
            result = mix(result, color4, 0.16 * pow(max(cross, 0.0), 3.0) * height);
            result = mix(result, color1, 0.48 * tie);
        """.trimIndent()

        PatternTextureType.BattenbergLace -> """
            vec2 g = p / medallionPitch + vec2(phaseX, phaseY), q = fract(g) - 0.5;
            float a = safeAngle(q), r = length(q), ro = 0.29 + 0.085 * cos(4.0 * a);
            float band = abs(r - ro) * medallionPitch;
            float tie = repeatDistance(a / TAU * brideCount) * TAU * max(r, 0.08) * medallionPitch;
            result = mix(color1, color2, 0.19 * layerNoise(p * 49.0));
            result = mix(result, color3, reliefBand(tie, 0.0025) * step(r, ro));
            result = mix(result, color4, reliefBand(band, tapeWidth * 0.53));
            result = mix(result, color3, 0.56 * reliefBand(abs(band - tapeWidth * 0.36), 0.0018));
        """.trimIndent()

        PatternTextureType.TatamiInlay -> """
            vec2 g = p / matWidth + 4.0 * vec2(phaseX, phaseY), superCell = floor(g * 0.5), q = mod(g, 2.0);
            bool turn = mod(superCell.x + superCell.y, 2.0) > 0.5;
            vec2 m = turn ? q.yx : q;
            float edge = min(repeatDistance(m.x), min(m.y, 2.0 - m.y));
            float fibre = cos(TAU * m.x * matWidth / rushPitch),
                  tie = repeatDistance(m.y * 0.5 * matWidth / (rushPitch * 4.0));
            vec4 reed = mix(color2, color4, 0.35 + 0.26 * fibre + 0.12 * noise(p * 96.0));
            reed = mix(reed, color3, 0.47 * reliefBand(tie, 0.052));
            result = mix(color1, reed, smoothstep(borderWidth, borderWidth + 0.016, edge));
        """.trimIndent()

        PatternTextureType.DarningSampler -> """
            vec2 g = p * 3.3 + 3.0 * vec2(phaseX, phaseY), id = mod(floor(g), 3.0), q = fract(g) - 0.5;
            vec2 yarn = q * yarnDensity;
            float warp = repeatDistance(yarn.x), weft = repeatDistance(yarn.y);
            float rx = patchWidth * 0.5 + frayedEnds * 0.09 * (noise(vec2(floor(yarn.y), id.x)) - 0.5);
            float ry =
                patchWidth * 0.48 + frayedEnds * 0.10 * (noise(vec2(floor(yarn.x), id.y + 7.0)) - 0.5);
            result = mix(color1, color2, 0.16 + 0.18 * cos(yarn.x * TAU) * cos(yarn.y * TAU));
            float m1 = reliefBand(warp, 0.20) * step(abs(q.y), ry) * step(abs(q.x), patchWidth * 0.53);
            float m2 = reliefBand(weft, 0.20) * step(abs(q.x), rx) * step(abs(q.y), patchWidth * 0.51);
            result = mix(result, color3, m1);
            result = mix(result, color4, m2);
            result = mix(result, color3, m1 * m2 * mod(floor(yarn.x + 0.5) + floor(yarn.y + 0.5), 2.0));
        """.trimIndent()

        PatternTextureType.ChenilleRows -> """
            vec2 q = rotatePoint(p, 0.67);
            float g = q.x / walePitch + 4.0 * phaseX;
            float y = q.y * 136.0, row = mod(floor(g + 0.5), 4.0),
                  end = noise(vec2(row, floor(y))) * pileFray * 0.16;
            float dx = fract(g + 0.5) - 0.5;
            float cut = 1.0 - smoothstep(pileWidth * 0.40 + end, pileWidth * 0.52 + end, abs(dx));
            float fringe = 0.5 + 0.5 * cos(TAU * y + 7.0 * abs(dx) + 2.0 * noise(vec2(row, y * 0.4)));
            result = mix(color1, color2, 0.31 * layerNoise(q * 44.0));
            vec4 pile = mix(color2, color4, 0.30 + 0.47 * fringe + 0.14 * sign(dx));
            pile = mix(pile, color3, 0.34 * noise(q * 53.0));
            result = mix(result, pile, cut);
        """.trimIndent()

        PatternTextureType.SpanishBlackwork -> """
            vec2 g = p / patternPitch + vec2(phaseX, phaseY), q = abs(fract(g) - 0.5) * 12.0;
            float border = abs(q.x + q.y - 5.0),
                  star = abs(max(q.x, q.y) * 0.82 + min(q.x, q.y) * 0.30 - 2.8);
            float grid = fract(g.x * 12.0) + fract(g.y * 12.0);
            float diag = repeatDistance(q.x - q.y);
            float fill = reliefBand(diag * patternPitch / 12.0, stitchGauge) *
                         step(q.x + q.y, 3.0 + fillDensity * 4.0) * step(0.26, fract((q.x + q.y) * 0.5));
            float thread = max(fill, max(reliefBand(border * patternPitch / 12.0, stitchGauge),
                                         reliefBand(star * patternPitch / 12.0, stitchGauge))) *
                           smoothstep(0.025, 0.14, grid);
            result = mix(color3, color4, 0.37 + 0.13 * cos(p.x * 700.0) * cos(p.y * 714.0));
            result = mix(result, mix(color1, color2, 0.15 + 0.24 * noise(p * 156.0)), thread);
        """.trimIndent()

        PatternTextureType.ShingleFishscale -> """
            vec2 g = p / vec2(shingleWidth, shingleWidth * 0.69) + 4.0 * vec2(phaseX, phaseY);
            result = color1;
            for (int j = 0; j < 3; j++) {
                float row = floor(g.y) + float(j) - 1.0;
                float shift = 0.5 * mod(row, 2.0);
                float ix = floor(g.x + shift), x = fract(g.x + shift) - 0.5, y = g.y - row;
                float lip = 0.44 + scallopDepth * sqrt(max(0.0, 1.0 - 4.0 * x * x));
                float d = max(abs(x) - 0.486, max(-0.16 - y, y - lip));
                float grain = layerNoise(vec2(x * 39.0 + mod(ix, 4.0), y * 3.0 + mod(row, 4.0)));
                vec4 wood =
                    mix(color2, color4,
                        0.14 + 0.32 * hash(mod(vec2(ix, row), 4.0)) + 0.45 * grainWeathering * grain);
                wood.rgb *= 0.92 + 0.14 * (1.0 - abs(x) * 1.5) - 0.23 * exp(-abs(0.01 + y) * 17.0);
                wood = mix(wood, color3, 0.62 * smoothstep(lip - 0.048, lip, y));
                result = mix(result, wood, coverage(d * shingleWidth, 0.0));
            }
        """.trimIndent()

        PatternTextureType.TwistedPaperCord -> """
            vec2 q = rotatePoint(p, -0.46);
            float g = q.x / cordWidth + phaseX, t = fract(g) - 0.5;
            float spiral = q.y / twistPitch + 0.5 * t + 0.08 * sin(q.y * 34.0 + fract(g) * TAU);
            float fold = fract(spiral), seam = repeatDistance(spiral);
            float height = sqrt(max(0.0, 1.0 - pow(abs(t) * 2.0, 2.0)));
            result = mix(color1, mix(color2, color4, 0.30 + 0.40 * height - 0.23 * t),
                         smoothstep(0.0, 0.09, 0.5 - abs(t)));
            result = mix(result, color2, creaseDepth * (1.0 - smoothstep(0.012, 0.15, seam)) * 0.79);
            result = mix(result, color3, creaseDepth * smoothstep(0.83, 0.94, fold) * 0.56);
            result.rgb *= 0.92 + 0.13 * layerNoise(q * vec2(109.0, 34.0));
        """.trimIndent()

        PatternTextureType.DrypointBurr -> """
            vec2 q = rotatePoint(p, -0.63);
            float line = q.y / strokePitch + 0.68 * sin(q.x * 11.0) + 0.43 * layerNoise(q * 5.0);
            float y = fract(line + 0.5) - 0.5;
            float burn = noise(vec2(q.x * 147.0, floor(line + 0.5)));
            result = mix(color4, color3, plateTone * (0.17 + 0.52 * layerNoise(q * vec2(23.0, 7.0))));
            float burr = exp(-max(y, 0.0) / burrWidth) * step(0.0, y) * (0.28 + 0.72 * burn);
            result = mix(result, color2, burr * 0.86);
            result = mix(result, color1, reliefBand(y * strokePitch, 0.0014 + 0.0013 * burn));
        """.trimIndent()

        PatternTextureType.MezzotintRocker -> """
            vec2 q = rotatePoint(p, radians(passAngle));
            float plate = layerNoise(p * 3.8);
            float pit = 0.0;
            for (int i = 0; i < 3; i++) {
                vec2 z = rotatePoint(q, float(i) * radians(passAngle)) * rockerTeeth;
                z.y += 0.29 * sin(z.x * 0.78 + float(i));
                z.x += 0.4 * floor(z.y);
                vec2 id = floor(z), local = fract(z) - 0.5 - 0.25 * (hash2(id) - 0.5);
                float tip = length(local * vec2(1.0, 0.66));
                pit = max(pit, 1.0 - smoothstep(0.13, 0.47, tip));
            }
            float scrape = smoothstep(0.66 - scrapedLight * 0.5, 0.92 - scrapedLight * 0.42, plate);
            result = mix(color1, color2, 0.19 + 0.33 * noise(p * 170.0));
            result =
                mix(result, mix(color3, color4, scrape), scrape * (1.0 - pit * (0.95 - 0.48 * scrape)));
        """.trimIndent()

        PatternTextureType.BenDayDots -> """
            float wash = layerNoise(p * 3.9);
            result = color1;
            for (int i = 0; i < 2; i++) {
                vec2 q = rotatePoint(p, i == 0 ? 0.09 : radians(screenAngle)) / screenPitch;
                vec2 t = fract(q) - 0.5;
                float radius = inkCoverage * (i == 0 ? 0.28 + 0.28 * wash : 0.44 - 0.20 * wash);
                float mask = coverage((length(t) - radius) * screenPitch, 0.0);
                if (i == 0)
                    result = mix(result, color2, mask);
                else {
                    float prior =
                        distance(result.rgb, color1.rgb) / max(0.01, distance(color2.rgb, color1.rgb));
                    result = mix(result, mix(color3, color4, clamp(prior, 0.0, 1.0)), mask);
                }
            }
        """.trimIndent()

        PatternTextureType.SplitFountainInk -> """
            vec2 q = rotatePoint(p, -0.22);
            float stripe =
                fract(q.x / rollerPitch + 0.045 * sin(q.y * 4.1) + 0.035 * layerNoise(q * vec2(12.0, 3.0)));
            float f1 = smoothstep(0.24 - blendWidth * 0.37, 0.24 + blendWidth * 0.37, stripe),
                  f2 = smoothstep(0.59 - blendWidth * 0.37, 0.59 + blendWidth * 0.37, stripe);
            result = mix(mix(color1, color2, f1), color3, f2);
            result = mix(result, color1, smoothstep(0.87 - blendWidth * 0.22, 1.0, stripe));
            float drag = layerNoise(q * vec2(183.0, 9.0));
            float skip = smoothstep(0.89 - inkSkip * 0.37, 0.98 - inkSkip * 0.31, drag);
            result = mix(result, color4, skip * 0.83 + 0.045 * noise(q * 302.0));
        """.trimIndent()

        PatternTextureType.StippleAquatint -> """
            vec2 q = p / rosinGrain;
            vec3 cells = cellSample(q * 1.7 + 0.28 * vec2(noise(q), noise(q + 16.0)));
            float field = layerNoise(p * 5.1);
            float stop = smoothstep(0.74 - stopoutArea * 0.44, 0.78 - stopoutArea * 0.44, field);
            float resin = smoothstep(0.24 + biteDepth * 0.24, 0.32 + biteDepth * 0.26, cells.x);
            result = mix(color1, color2, 0.14 + 0.47 * cells.z);
            result = mix(result, color3, resin * (0.67 + 0.26 * cells.z));
            result = mix(result, color4, stop * (0.63 + 0.28 * noise(q * 3.0)));
        """.trimIndent()

        PatternTextureType.ChineColle -> """
            vec2 g = p / paperFragmentSize;
            result = mix(color3, color4, 0.54 + 0.18 * layerNoise(p * 46.0));
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(g) + vec2(float(i), float(j)),
                         v = g - id - 0.5 - 0.48 * (hash2(id) - 0.5);
                    v = rotatePoint(v, (hash(id) - 0.5) * 1.1);
                    float d = max(abs(v.x) - 0.42, max(abs(v.y) - 0.31, v.x * 0.64 + v.y - 0.37));
                    d += deckleDepth * 0.075 * (layerNoise(p * 55.0 + id) - 0.5);
                    vec4 tissue = mix(color2, color3, hash(id + 8.0));
                    result = mix(result, tissue, coverage(d * paperFragmentSize, 0.0) * 0.78);
                }
            float strokes = 0.5 + 0.5 * cos((p.y + 0.085 * sin(p.x * 11.0)) * TAU * 27.0);
            result = mix(result, color1,
                         inkVeil * pow(strokes, 7.0) * smoothstep(0.37, 0.62, layerNoise(p * 7.0)));
        """.trimIndent()

        PatternTextureType.DeckledPulp -> """
            float clump = layerNoise(p * 18.0);
            result = mix(color3, color4, 0.44 + pulpClumps * (clump - 0.35));
            vec2 g = p * 31.0;
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(g) + vec2(float(i), float(j));
                    vec2 v = (g - id - hash2(id)) / 31.0;
                    float h = hash(id + 8.0);
                    v = rotatePoint(v, (h - 0.5) * TAU);
                    float y = v.y - 0.003 * sin(v.x * 83.0 + h * 6.0);
                    float d =
                        segment(vec2(v.x, y), vec2(-fibreLength * 0.5, 0.0), vec2(fibreLength * 0.5, 0.0));
                    vec4 thread = mix(color1, color2, h);
                    result =
                        mix(result, thread,
                            coverage(d, 0.0013) * (0.21 + 0.57 * step(hash(id + 9.0), colouredFibres)));
                }
        """.trimIndent()

        PatternTextureType.LeatherBasketStamp -> """
            vec2 g = rotatePoint(p, 0.64) / stampPitch + 2.0 * vec2(phaseX, phaseY);
            vec2 id = floor(g), q = fract(g) - 0.5;
            bool turned = mod(id.x + id.y, 2.0) > 0.5;
            q = turned ? q.yx : q;
            float d = max(abs(q.x) - 0.397, abs(q.y) - 0.318);
            float rib =
                pow(max(0.0, cos(TAU * q.x * ribCount)), 3.0) * (1.0 - smoothstep(0.28, 0.35, abs(q.y)));
            float grain = layerNoise(p * 100.0);
            result = mix(color2, color3, 0.25 + 0.42 * grain);
            result = mix(result, color1, toolDepth * 0.68 * exp(-abs(d) * 27.0));
            result = mix(result, color4, 0.55 * toolDepth * exp(-abs(d - 0.032) * 37.0) * step(q.y, 0.0));
            result = mix(result, color1, toolDepth * 0.47 * rib * coverage(d * stampPitch, 0.0));
        """.trimIndent()

        PatternTextureType.AdzeHewnOak -> """
            vec2 q = rotatePoint(p, -0.14);
            float lane = floor(q.x / adzeWidth), x = fract(q.x / adzeWidth) - 0.5;
            float y = q.y / (adzeWidth * 1.42) + hash(vec2(lane, 3.0));
            float row = floor(y);
            y = fract(y) - 0.5;
            float wobble = 0.16 * (hash(vec2(lane, row)) - 0.5);
            float lip = y + 0.93 * (x + wobble) * (x + wobble) - 0.23;
            float gouge = clamp(1.0 - pow(abs(x) * 1.88, 2.0) - pow(abs(y + 0.12) * 1.66, 2.0), 0.0, 1.0);
            float fibres = layerNoise(q * vec2(121.0, 10.0));
            result = mix(color2, color3, 0.27 + 0.36 * fibres + 0.19 * hash(vec2(lane, row)));
            result = mix(result, color1,
                         cutDepth * 0.80 * exp(-abs(lip) * 16.0) * (0.45 + 0.55 * gouge) +
                             0.31 * tornGrain * smoothstep(0.67, 0.85, fibres));
            result = mix(result, color4, cutDepth * 0.53 * gouge * (0.75 - 0.63 * y));
            result = mix(result, color2,
                         tornGrain * 0.4 * pow(max(0.0, cos(q.x * 136.0 + fibres * 9.0)), 7.0) * gouge);
        """.trimIndent()

        PatternTextureType.CloisonneCells -> """
            vec2 q = p / compartmentSize;
            vec2 bend = vec2(sin(q.y * 1.6 + 0.45 * sin(q.x)), sin(q.x * 1.3 + 0.7 * cos(q.y)));
            vec3 v = cellSample(q + 0.31 * bend);
            vec4 enamel = mix(color1, mix(color2, color3, step(0.55, v.z)), 0.39 + 0.55 * v.z);
            float glass = 1.0 - exp(-v.y * 5.5);
            enamel.rgb *= 0.76 + 0.25 * glass +
                          0.13 * enamelCurvature * max(0.0, cos(q.x * 2.0 + q.y * 1.1 + v.z * 6.0));
            enamel = mix(enamel, color4, 0.14 * enamelCurvature * exp(-abs(v.y - 0.13) * 18.0));
            result = mix(enamel, color4,
                         1.0 - smoothstep(wireGauge * 0.6, wireGauge * 1.35, v.y * compartmentSize));
        """.trimIndent()

        PatternTextureType.LaidPaperWatermark -> """
            vec2 q = rotatePoint(p, 0.037);
            float laid = 0.5 + 0.5 * cos(TAU * q.y / laidPitch),
                  chain =
                      1.0 - smoothstep(0.001, 0.006, repeatDistance(q.x / chainInterval) * chainInterval);
            result = mix(color2, color4, 0.53 + 0.17 * laid + 0.12 * chain + 0.13 * layerNoise(p * 41.0));
            vec2 g = fract(p * 2.4) - 0.5;
            float a = safeAngle(g), r = length(g);
            float flower = abs(r - (0.20 + 0.052 * cos(6.0 * a)));
            float bezel = min(abs(r - 0.32), abs(r - 0.345));
            float petal = abs(length(vec2(g.x * 0.78, g.y)) - 0.115);
            float emboss = exp(-min(min(flower, bezel), petal) * 116.0);
            result = mix(result, mix(color1, color3, 0.38), markOpacity * 0.83 * emboss);
        """.trimIndent()

        PatternTextureType.ShellacCheckering -> """
            vec2 q = rotatePoint(p, -0.13);
            float wood = layerNoise(q * vec2(7.0, 65.0));
            result = mix(color2, color3, 0.20 + 0.61 * wood);
            result = mix(result, color1, 0.35 * glazeTint);
            vec2 g = q / vec2(checkSpacing * 1.7, checkSpacing * 0.72);
            g += 0.18 * vec2(noise(g * 1.7), noise(g * 1.8 + 9.0));
            float longLine = repeatDistance(g.y);
            float crossLine = repeatDistance(g.x + 0.38 * floor(g.y + 0.5));
            float edge = min(longLine, crossLine * (1.32 + 0.56 * hash(floor(g))));
            result = mix(result, color1,
                         reliefBand(edge, 0.019 + 0.049 * openCracks) * (0.45 + 0.46 * openCracks));
            result = mix(result, color4,
                         0.27 * exp(-abs(edge - 0.074) * 25.0) +
                             0.14 * pow(max(0.0, sin(q.y * 26.0 + wood * 2.0)), 4.0));
        """.trimIndent()

        PatternTextureType.CombedSlip -> """
            vec2 q = rotatePoint(p, -0.16);
            float stroke = q.x / 0.24;
            float tooth = fract(q.y / combPitch + 0.11 * sin(q.x * 9.0));
            float drag =
                drawDepth * (0.44 - 0.88 * repeatDistance(stroke)) * (0.48 + 0.52 * cos(TAU * tooth));
            float ribbon = fract(q.y / 0.15 + drag);
            result = mix(color2, color3, smoothstep(slipRatio - 0.025, slipRatio + 0.025, ribbon));
            result = mix(
                result, color4,
                0.73 * smoothstep(0.70, 0.93, ribbon) * smoothstep(slipRatio, slipRatio + 0.10, ribbon));
            result = mix(result, color1,
                         0.50 * reliefBand(repeatDistance(q.y / combPitch + drag * 0.55), 0.033));
            result.rgb *= 0.94 + 0.10 * layerNoise(q * 24.0);
        """.trimIndent()

        PatternTextureType.ParquetDeVersailles -> """
            vec2 g = p / panelPitch + vec2(phaseX, phaseY), q = fract(g) - 0.5;
            float edge = 0.5 - max(abs(q.x), abs(q.y));
            vec2 bias = rotatePoint(q, 0.785398) / slatWidth;
            vec2 d = vec2(repeatDistance(bias.x), repeatDistance(bias.y));
            bool rail = d.x < 0.25 || d.y < 0.25;
            bool longX = (d.x < 0.25 && d.y < 0.25)
                             ? mod(floor(bias.x + 0.5) + floor(bias.y + 0.5), 2.0) < 0.5
                             : d.x < 0.25;
            vec2 fibre = longX ? bias : bias.yx;
            float veining = layerNoise(fibre * vec2(18.0, 2.4));
            result = mix(color2, color4,
                         rail ? 0.36 + 0.53 * veining : 0.12 + 0.34 * noise(bias * vec2(5.0, 18.0)));
            float seam = min(abs(d.x - 0.25), abs(d.y - 0.25));
            result = mix(result, color1, 0.55 * reliefBand(seam, 0.023));
            vec2 frame = abs(q.x) > abs(q.y) ? q : q.yx;
            vec4 border = mix(color1, color3, 0.37 + 0.42 * layerNoise(frame * vec2(75.0, 9.0)));
            result = mix(border, result, smoothstep(frameWidth, frameWidth + 0.009, edge));
            result = mix(result, color1, 0.63 * reliefBand(edge - frameWidth, 0.005));
        """.trimIndent()

        PatternTextureType.ScallopValves -> """
            vec2 q = p * 3.35;
            result = mix(color1, color2, .17 + .31 * layerNoise(q * 5.0));
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 id = floor(q) + vec2(float(x), float(y));
                    vec2 center = id + vec2(.5 + .19 * (hash(id) - .5), .63);
                    vec2 uv = rotatePoint(q - center, (hash(id + 6.0) - .5) * 1.3);
                    float r = length(uv);
                    float a = abs(atan(uv.x, -uv.y));
                    float rib = .5 + .5 * cos(safeAngle(vec2(-uv.y, uv.x)) * ribs * 3.14159 / valveFan +
                                              0.23 * sin(r * 16.0));
                    float edge = .47 + .058 * rib + .042 * hash(id + 2.0);
                    float mask = (1.0 - smoothstep(valveFan - .04, valveFan + .04, a)) *
                                 (1.0 - smoothstep(edge - .008, edge + .01, r));
                    vec4 shell =
                        mix(color1, color4, .23 + .63 * pow(rib, .73) * lipHeight + .19 * noise(uv * 23.0));
                    shell = mix(shell, color3, .14 + .27 * (1.0 - smoothstep(.11, .41, r)));
                    float growth = .5 + .5 * cos(r * 74.0 + .7 * sin(a * 8.0));
                    shell = mix(shell, color2, .22 * pow(growth, 3.0));
                    float hinge = max(abs(uv.x) - .15, abs(uv.y + .015) - .028);
                    result = mix(result, color3, 1.0 - smoothstep(.002, .014, hinge));
                    shell = mix(shell, color4,
                                .50 * (1.0 - smoothstep(.011, .044, abs(r - edge + .042))) +
                                    .10 * (.5 + .5 * cos(r * 130.0)));
                    result = mix(result, shell, mask);
                }
        """.trimIndent()

        PatternTextureType.UrchinTest -> """
            vec2 q = p / platePitch;
            float bow = .28 * sin(q.y * .63);
            q.x += bow;
            float row = floor(q.y);
            q.x += .5 * mod(row, 2.0);
            vec2 id = floor(q), uv = fract(q) - .5;
            float bevel = max(abs(uv.x) * 1.6, abs(uv.y) * 1.26 + abs(uv.x) * .6);
            result = mix(color2, color3, .23 + .42 * hash(id) + .17 * noise(q * 13.0));
            result = mix(result, color1, smoothstep(.78, .84, bevel) * .58);
            float d = length(uv);
            result = mix(result, color4, (1.0 - smoothstep(tubercles * .63, tubercles * 1.37, d)) * .87);
            result = mix(result, color1, (1.0 - smoothstep(tubercles * .22, tubercles * .49, d)) * .68);
            vec2 holes = vec2(abs(uv.x) - .32, repeatDistance(uv.y / porePairs) * porePairs);
            float pore = length(holes * vec2(1.1, .72));
            result = mix(result, color4, 1.0 - smoothstep(.028, .046, pore));
            result = mix(result, color1, 1.0 - smoothstep(.015, .030, pore));
        """.trimIndent()

        PatternTextureType.NummuliteBank -> """
            vec2 q = rotatePoint(p, .32) * 4.75;
            vec4 cell = surfaceCell(q, .58);
            vec2 id = floor(q - cell.zw);
            vec2 uv = rotatePoint(cell.zw, hash(id + 4.0) * 3.14159);
            uv.y *= .94 + .91 * hash(id + 14.0);
            float r = length(uv);
            float radius = fossilSize * (.85 + .31 * hash(id + 7.0));
            float a = safeAngle(uv) / TAU;
            float radial = r / radius;
            float path = radial * whorls + a + .020 * sin(TAU * a * 3.0);
            float wall = 1.0 - smoothstep(.045, .178, repeatDistance(path));
            float turns = floor(path + .5);
            float bars = repeatDistance(a * septalCount + .31 * fract(path + .5) + .19 * turns);
            float chamber = 1.0 - smoothstep(.030, .122, bars);
            vec4 matrix = mix(color1, color2, .25 + .53 * layerNoise(q * 13.0));
            vec4 shell =
                mix(color3, color4,
                    .27 + .21 * noise(q * 43.0) + .21 * hash(vec2(turns, floor(a * septalCount + .5))));
            shell = mix(shell, color2, .84 * chamber);
            shell = mix(shell, color1, wall * .88);
            shell = mix(shell, color2, smoothstep(.93, 1.02, radial) * .68);
            result =
                mix(matrix, shell, (1.0 - smoothstep(.962, 1.02, radial)) * smoothstep(.012, .067, cell.y));
        """.trimIndent()

        PatternTextureType.CrinoidColumnals -> """
            vec2 q = p * 6.0;
            vec4 cell = surfaceCell(q, .79);
            vec2 id = floor(q - cell.zw);
            vec2 uv = rotatePoint(cell.zw, hash(id) * 4.2);
            uv.y *= 1.0 + .64 * hash(id + 3.0);
            float r = length(uv), a = safeAngle(uv);
            float radius = jointSize * (.73 + .5 * hash(id + 2.0));
            float grooves = .5 + .5 * cos(a * facetCount);
            float star = .70 + .30 * cos(5.0 * a);
            vec4 stone = mix(color1, color2, .23 + .57 * layerNoise(q * 9.0));
            vec4 joint = mix(color3, color4, .18 + .57 * grooves * smoothstep(lumenSize, radius * .91, r));
            joint = mix(joint, color2, (1.0 - smoothstep(.013, .035, abs(r - radius * .84))) * .71);
            joint = mix(joint, color1, 1.0 - smoothstep(lumenSize * star, lumenSize * star + .01, r));
            result =
                mix(stone, joint,
                    (1.0 - smoothstep(radius - .008, radius + .007, r)) * smoothstep(.018, .067, cell.y));
        """.trimIndent()

        PatternTextureType.GraptoliteShale -> """
            vec2 q = p * 4.0;
            vec2 id = floor(q);
            vec2 uv = rotatePoint(fract(q) - .5, (hash(id) - .5) * 5.1);
            result = mix(color1, color2, .33 + .44 * layerNoise(p * vec2(23.0, 96.0)));
            float t = uv.y + .39;
            float x = abs(uv.x) - t * .36 - .035 * sin(6.0 * t);
            float outline = 1.0 - smoothstep(.006, .020, abs(x));
            float cups = fract(t / stipeLength * thecae);
            float spine = 1.0 - smoothstep(.007, .02, abs(x - .073 * cups));
            spine *= smoothstep(.00, .20, cups) * (1.0 - smoothstep(.67, .94, cups));
            float fossil = max(outline, spine) * smoothstep(.0, .03, t) *
                           (1.0 - smoothstep(stipeLength - .04, stipeLength, t));
            result = mix(result, mix(color3, color4, fossilMetal * (.21 + .68 * noise(q * 42.0))), fossil);
        """.trimIndent()

        PatternTextureType.StromatoliteDomes -> """
            vec2 q = rotatePoint(p, .09) * 3.6;
            float bed = floor(q.y);
            float layer = q.y;
            float arc = 0.0;
            for (int i = -1; i <= 1; i++) {
                float col = floor(q.x) + float(i), r = .41 + .21 * hash(vec2(col, bed));
                float x = (q.x - col - .48) / r;
                arc = max(arc, sqrt(max(0.0, 1.0 - x * x)));
            }
            float seams = q.y - domeHeight * arc + .10 * matWobble * layerNoise(q * vec2(12.0, 8.0));
            float lamina = fract(seams / laminae);
            result = mix(color1, color2, .31 + .56 * layerNoise(q * 8.0));
            result =
                mix(result, color3, smoothstep(.15, .46, lamina) * (1.0 - smoothstep(.67, .88, lamina)));
            result = mix(result, color4, (1.0 - smoothstep(.04, .12, abs(lamina - .73))) * .73);
        """.trimIndent()

        PatternTextureType.SeptarianNodules -> """
            vec2 q = p * 3.4;
            vec4 knot = surfaceCell(q, .73);
            vec2 id = floor(q - knot.zw);
            float r = length(knot.zw * vec2(1.04, 1.24));
            float radius = noduleSize * (.87 + .28 * hash(id + 2.0));
            vec2 micro = knot.zw * 6.6 + id * 9.1 + .22 * noise(q * 21.0);
            vec4 crack = surfaceCell(micro, .80);
            float dist = crack.y + .08 * noise(q * 41.0);
            result = mix(color1, color2, .37 + .43 * layerNoise(q * 9.0));
            vec4 matrix = mix(color1, color2, .49 + .41 * noise(q * 23.0));
            matrix = mix(matrix, color3, 1.0 - smoothstep(veinGauge, veinGauge + .11 * oxideRim, dist));
            matrix = mix(matrix, color4, 1.0 - smoothstep(veinGauge * .42, veinGauge + .022, dist));
            matrix = mix(matrix, color1, smoothstep(radius - .05 * oxideRim, radius, r) * .78);
            result =
                mix(result, matrix,
                    (1.0 - smoothstep(radius - .014, radius + .012, r)) * smoothstep(.012, .07, knot.y));
        """.trimIndent()

        PatternTextureType.TourmalinePrisms -> """
            vec2 q = p * 4.5;
            vec4 cell = surfaceCell(q, .76);
            vec2 id = floor(q - cell.zw);
            vec2 uv = rotatePoint(cell.zw, hash(id) * TAU);
            float angle = safeAngle(uv);
            float taper = cos(mod(angle + .5236, 1.0472) - .5236);
            float r = length(uv) * taper / prismRadius;
            result = mix(color1, color4, .33 + .43 * layerNoise(q * 13.0));
            vec4 prism = mix(color3, color4, .19 + .29 * noise(q * 26.0));
            prism = mix(prism, color4, smoothstep(coreFraction - .04, coreFraction + .04, r));
            prism = mix(prism, color2, smoothstep(coreFraction + .06, coreFraction + .18, r));
            prism.rgb *=
                .83 + .23 * zoning * cos(r * 72.0 + 1.4 * noise(q * 15.0)) + .18 * fract(angle / 1.0472);
            result = mix(result, prism, (1.0 - smoothstep(.99, 1.035, r)) * smoothstep(.02, .071, cell.y));
        """.trimIndent()

        PatternTextureType.SeleniteBlades -> """
            vec2 q = p * 5.2;
            result = mix(color1, color2, .32 + .45 * layerNoise(q * 4.0));
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    vec2 uv = rotatePoint(q - id - hash2(id), .52 + (hash(id + 7.0) - .5) * 1.6);
                    float span = bladeLength * (.61 + .60 * hash(id + 3.0));
                    float tip = abs(uv.y) + 1.9 * abs(uv.x);
                    float mask = (1.0 - smoothstep(bladeWidth, bladeWidth + .013, abs(uv.x))) *
                                 (1.0 - smoothstep(span, span + .017, tip));
                    float facet = uv.x > bladeWidth * .15 ? .64 : .20;
                    float satin = .5 + .5 * cos(uv.x * 239.0 + .7 * sin(4.0 * uv.y));
                    vec4 splinter = mix(color2, color4, facet + silkFlash * .28 * satin);
                    splinter = mix(splinter, color3, smoothstep(span - .14, span, tip) * .65);
                    result = mix(result, splinter, .84 * mask);
                }
        """.trimIndent()

        PatternTextureType.GraphicGranite -> """
            vec2 q = rotatePoint(p, .40) * 15.0 / glyphScale;
            vec4 block = surfaceCell(q * .43, .73);
            vec2 id = floor(q * .43 - block.zw);
            vec2 uv = rotatePoint(q, hash(id) * 2.7);
            float cuts = abs(fract(uv.x + .35 * floor(uv.y)) - .5);
            float stair = abs(fract(uv.y * .84 + .24 * floor(uv.x)) - .5);
            float shard = max(cuts * .62 + stair * .83 - intergrowth * .38, abs(stair - cuts * .43) - .08);
            float quartz = 1.0 - smoothstep(.026, .075, shard + .09 * noise(q * 4.0));
            result = mix(color3, color4, .26 + feldsparGrain * .55 * layerNoise(q * 6.0));
            result = mix(result, mix(color1, color2, .28 + .44 * noise(q * 19.0)), quartz);
        """.trimIndent()

        PatternTextureType.PillowBasalt -> """
            vec2 q = rotatePoint(p, -.18) * 6.2 / vec2(lobeAspect, 1.0);
            q += .14 * vec2(noise(q * 1.6), noise(q * 1.6 + 7.0));
            vec4 cell = surfaceCell(q, .67);
            float skin = smoothstep(.015, rindWidth + .025, cell.y);
            float a = safeAngle(cell.zw) / TAU;
            float joints =
                1.0 - smoothstep(.018, .066, repeatDistance(a * radialCracks + .19 * noise(q * 3.0)));
            vec4 rock = mix(color2, color3, .21 + .51 * layerNoise(q * 9.0));
            rock = mix(rock, color1, .65 * joints * smoothstep(.20, .55, cell.x));
            result = mix(color1, rock, skin);
            result = mix(result, color4, (1.0 - smoothstep(.009, .045, abs(cell.y - rindWidth))) * .48);
        """.trimIndent()

        PatternTextureType.ChevronFolds -> """
            vec2 q = rotatePoint(p, -.16);
            float phase = q.x * 3.15 + .17 * q.y + .03 * noise(p * 5.0);
            float turn = fract(phase) - .5;
            float hinge = sqrt(turn * turn + hingeRound * hingeRound);
            float strata = (q.y - foldAngle * hinge + .025 * noise(q * vec2(6.0, 9.0))) / bedWidth;
            float band = floor(strata);
            float t = fract(strata);
            result = toneRamp(.17 + .73 * hash(vec2(band, 4.0)));
            result = mix(result, color1, .54 * (1.0 - smoothstep(.013, .10, min(t, 1.0 - t))));
            result.rgb *= .91 + .16 * layerNoise(q * vec2(89.0, 141.0));
        """.trimIndent()

        PatternTextureType.OrbicularDiorite -> """
            vec2 q = p * 4.5;
            vec4 cell = surfaceCell(q, .65);
            vec2 id = floor(q - cell.zw);
            vec2 uv = rotatePoint(cell.zw, hash(id) * TAU) * vec2(1.23, .92);
            float r = (length(uv) + .026 * noise(q * 9.0)) / (orbRadius * (.81 + .33 * hash(id + 5.0)));
            float grit = noise(q * 51.0), mac = layerNoise(q * 8.0);
            result = mix(color1, color2, .28 + .47 * mac);
            float shell = .5 + .5 * cos(TAU * (r * mantleCount + .035 * sin(safeAngle(uv) * 8.0)));
            vec4 orb = mix(color1, color3, .16 + .67 * shell);
            orb = mix(orb, color4, .33 * crystalFleck * smoothstep(.71, .91, grit));
            orb = mix(orb, mix(color2, color4, grit), 1.0 - smoothstep(.20, .30, r));
            result = mix(result, orb, (1.0 - smoothstep(.96, 1.01, r)) * smoothstep(.018, .07, cell.y));
        """.trimIndent()

        PatternTextureType.DesertRoses -> """
            vec2 q = rotatePoint(p, -0.21) * 2.55;
            float sand = layerNoise(q * 21.0);
            result = mix(color1, color2, .29 + .55 * sand);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    vec2 center = id + .43 + .46 * (hash2(id) - .5);
                    vec2 delta = q - center;
                    float clusterSize = .72 + .69 * hash(id + 3.0);
                    if (dot(delta, delta) > .93 * clusterSize * clusterSize)
                        continue;
                    for (int k = 0; k < 14; k++) {
                        if (float(k) >= bladeFans)
                            break;
                        float n = float(k);
                        float a = 6.283185 * (n * .382 + hash(id)) + .42 * (hash(id + n + 5.0) - .5);
                        vec2 v = rotatePoint(delta / clusterSize, a);
                        float reach = crystalSpan * (1.06 + .61 * hash(id + n + 8.0));
                        float y = v.y + .17 * reach;
                        float t = y / reach;
                        float broad = .079 + .078 * hash(id + n + 18.0);
                        float plate = broad * (.45 + .60 * smoothstep(-.05, .52, t));
                        float x = v.x - .031 * sin(t * 2.7 + n);
                        float back =
                            max((abs(x) - plate) / broad, max(-t - .05, t - 1.0 + abs(x / broad) * .24));
                        float shade = max((abs(x - .020) - plate) / broad,
                                          max(-t - .09, t - 1.04 + abs(x / broad) * .22));
                        result = mix(result, color1, .49 * (1.0 - smoothstep(.00, .09, shade)));
                        if (back < .08) {
                            float bevel = smoothstep(-.10, .12, x / plate + .18 * t);
                            vec4 blade = mix(color2, color4, .24 + .55 * bevel + .13 * hash(id + n + 16.0));
                            float facet = smoothstep(.69, .91, t + .21 * abs(x / plate));
                            blade = mix(blade, color3, .59 * facet);
                            float cleavage = repeatDistance(y * 26.0 + x * 4.0 + .1 * noise(v * 21.0));
                            blade = mix(blade, color1, (1.0 - smoothstep(.01, .056, cleavage)) * .23);
                            blade = mix(blade, color2, sandCoat * smoothstep(.49, .78, sand) * .87);
                            result = mix(result, blade, 1.0 - smoothstep(-.03, .08, back));
                        }
                    }
                }
        """.trimIndent()

        PatternTextureType.RippleCrossbeds -> """
            vec2 q = rotatePoint(p, -.09);
            float cut = q.y + .027 * sin(q.x * 8.0) + .01 * noise(p * 16.0);
            float bed = floor(cut / setDepth), local = fract(cut / setDepth);
            float angle = (hash(vec2(bed, 1.0)) > .48 ? 1.0 : -1.0) * dipAngle;
            float lamina =
                (q.y + angle * (q.x + .059 * sin(q.x * 13.0 + bed)) + .11 * setDepth * local * local) /
                laminaPitch;
            float band = .5 + .5 * cos(TAU * lamina);
            result = mix(color2, color3, .29 + .43 * layerNoise(q * 27.0));
            result = mix(result, color1, .43 * smoothstep(.69, .95, band));
            result = mix(result, color4, .50 * (1.0 - smoothstep(.012, .085, min(local, 1.0 - local))));
        """.trimIndent()

        PatternTextureType.MangroveKnees -> """
            vec2 q = rotatePoint(p, -.06) * 5.25;
            result = mix(color1, color2, .25 + .45 * layerNoise(p * vec2(19.0, 34.0)));
            result = mix(result, color3, mudCover * smoothstep(.4, .72, noise(q * .75)) * .49);
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 id = floor(q) + vec2(float(x), float(y));
                    vec2 uv = q - id - .27 - .47 * hash2(id);
                    float h = rootHeight * (.51 + .55 * hash(id + 3.0));
                    float radial = length(uv * vec2(1.0, 2.2));
                    float rings = (1.0 - smoothstep(.026, .11, repeatDistance(radial * 14.0))) *
                                  (1.0 - smoothstep(rippleReach * .45, rippleReach, radial));
                    result = mix(result, color4, rings * .37);
                    float t = -uv.y / h;
                    float w = .105 * (1.0 - clamp(t, 0.0, 1.0)) + .016;
                    float tip = (1.0 - smoothstep(w, w + .014, abs(uv.x - .04 * sin(t * 2.5)))) *
                                smoothstep(-.045, .02, t) * (1.0 - smoothstep(.9, 1.0, t));
                    result = mix(result, mix(color2, color3, .28 + .56 * smoothstep(-w, w, -uv.x)), tip);
                }
        """.trimIndent()

        PatternTextureType.CottonBolls -> """
            vec2 q = p * 3.8;
            q.x += .43 * mod(floor(q.y), 2.0);
            vec2 id = floor(q);
            vec2 uv = rotatePoint(fract(q) - .5, hash(id) * TAU);
            float r = length(uv), a = safeAngle(uv);
            result = mix(color1, color2, .16 + .35 * layerNoise(p * 15.0));
            float bract = bractLength * (.32 + .68 * pow(.5 + .5 * cos(a * lockCount), 4.0));
            result = mix(result, color2, 1.0 - smoothstep(bract, bract + .012, r));
            for (int i = 0; i < 6; i++) {
                if (float(i) >= lockCount)
                    break;
                float angle = TAU * (float(i) + .48) / lockCount;
                vec2 v = uv - .148 * vec2(cos(angle), sin(angle));
                float d = length(v) + .050 * (noise(v * 22.0) - .5);
                float mask = 1.0 - smoothstep(fluffSize * .87, fluffSize * 1.05, d);
                vec4 tuft =
                    mix(color3, color4,
                        .19 + .49 * (1.0 - smoothstep(0.0, fluffSize, d)) + .27 * layerNoise(v * 54.0));
                result = mix(result, tuft, mask);
            }
        """.trimIndent()

        PatternTextureType.BirchLenticels -> """
            vec2 q = rotatePoint(p, -.06);
            float mottling = layerNoise(q * vec2(8.0, 23.0));
            result = mix(color3, color4, .44 + .46 * mottling);
            result = mix(result, color2, barkTone * smoothstep(.46, .77, mottling) * .82);
            float row = q.y * 17.0;
            float line = repeatDistance(row + .045 * noise(q * 37.0));
            float x = q.x * 3.5 + hash(vec2(floor(row + .5), 2.0));
            float lens = (1.0 - smoothstep(.021, .094, line)) *
                         (1.0 - smoothstep(lenticelSpan * .52, lenticelSpan, repeatDistance(x)));
            result = mix(result, color1, lens * .91);
            float peel = repeatDistance(q.y * 4.7 + .12 * noise(q * vec2(7.0, 3.0)));
            result =
                mix(result, color2,
                    (1.0 - smoothstep(.002, peelWidth, peel)) * smoothstep(.37, .72, noise(q * 6.0)) * .75);
        """.trimIndent()

        PatternTextureType.PlaneBark -> """
            vec2 q = p * 4.3 / patchSize;
            vec2 drift = .64 * vec2(noise(q * 1.17), noise(q * 1.17 + 12.0));
            float patch = layerNoise((q + drift) * 1.27);
            float level = peeling * .26;
            result = color1;
            result = mix(result, color2, smoothstep(.36 - level * .36, .372 - level * .36, patch));
            result = mix(result, color3, smoothstep(.51 - level * .43, .52 - level * .43, patch));
            result = mix(result, color4, smoothstep(.65 - level * .45, .66 - level * .45, patch));
            float lip = min(abs(patch - .51 + level * .43), abs(patch - .65 + level * .45));
            result.rgb *= 1.0 - .27 * (1.0 - smoothstep(.002, lipWidth, lip));
            result.rgb *= .95 + .087 * noise(q * 49.0);
        """.trimIndent()

        PatternTextureType.CattailFloss -> """
            vec2 q = rotatePoint(p, -.24) * tuftDensity;
            result = mix(color1, color2, .17 + .41 * layerNoise(q * 3.1));
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    vec2 center = id + .10 + .80 * hash2(id);
                    vec2 uv = rotatePoint(q - center, .62 + (hash(id + 9.0) - .5) * 2.86);
                    float t = uv.y / fiberLength;
                    if (t > 0.0 && t < 1.21) {
                        float wind = .19 * (hash(id + 23.0) - .28);
                        float spread = fanSpread * (.11 * t + .42 * t * t);
                        float bend = wind * t * t + .043 * sin(t * 3.4 + hash(id) * 5.0) * t;
                        float bundle = (uv.x - bend) / max(.008, spread) * 9.0;
                        float light = 0.0;
                        for (int k = -1; k <= 1; k++) {
                            float hair = floor(bundle + .5) + float(k);
                            if (abs(hair) > 9.5)
                                continue;
                            float variation = hash(id + vec2(hair * 1.33, 37.0));
                            float lengthRatio = .54 + .66 * variation;
                            float endT = t / lengthRatio;
                            float fiber =
                                bend + (hair + .35 * sin(4.6 * t + variation * TAU)) * spread / 9.0;
                            fiber += .016 * sin(6.0 * t + variation * 6.0) * t;
                            float width = .0022 + .0024 * (1.0 - clamp(endT, 0.0, 1.0));
                            float strand = 1.0 - smoothstep(width * .58, width + .006, abs(uv.x - fiber));
                            light = max(light, strand * smoothstep(.008, .115, t) *
                                                   (1.0 - smoothstep(.74, 1.02, endT)));
                        }
                        result = mix(result, mix(color3, color4, .32 + .50 * t), light * .95);
                    }
                    float achene = length((uv + vec2(.006, .043)) * vec2(3.7, 1.0));
                    result = mix(result, color3, 1.0 - smoothstep(.052, .091, achene));
                    result = mix(result, color1, (1.0 - smoothstep(.023, .053, achene)) * .75);
                }
        """.trimIndent()

        PatternTextureType.FernSori -> """
            vec2 q = rotatePoint(p, .43);
            float row = floor(q.y / frondPitch);
            q.x += .03 * sin(q.y * 8.0);
            float u = fract(q.x * 2.6 + .42 * mod(row, 2.0)) - .5;
            float v = fract(q.y / frondPitch) - .5 - .20 * sin(u * 4.0);
            float width = .31 * pow(max(0.0, 1.0 - abs(u) * 2.05), .58);
            float leaf = 1.0 - smoothstep(width, width + .025, abs(v));
            result = mix(color1, color2, .26 * layerNoise(p * 14.0));
            vec4 tissue = mix(color1, color2, .45 + .29 * noise(q * 49.0) + .15 * step(0.0, v));
            float rachis = 1.0 - smoothstep(.014, .04, abs(v));
            tissue = mix(tissue, color3, .55 * rachis);
            vec2 sori =
                vec2(repeatDistance((u + .5) * sorusCount) / sorusCount, (abs(v) - width * .56) * .47);
            float d = length(sori);
            tissue = mix(tissue, color3, 1.0 - smoothstep(sorusSize * .38, sorusSize * .72, d));
            tissue = mix(
                tissue, color4,
                (1.0 - smoothstep(sorusSize * .21, sorusSize * .51, d)) * (.40 + .56 * noise(q * 213.0)));
            result = mix(result, tissue, leaf);
        """.trimIndent()

        PatternTextureType.EquisetumCrosscuts -> """
            vec2 q = p * 4.6;
            vec4 cell = surfaceCell(q, .64);
            vec2 uv = cell.zw;
            float a = safeAngle(uv), d = length(uv);
            float ridged = stalkRadius * (.93 + .055 * cos(a * canalCount));
            result = mix(color1, color2, .22 + .41 * layerNoise(q * 5.0));
            vec4 cut = mix(color2, color3, .32 + .43 * noise(q * 42.0));
            cut = mix(cut, color4, .49 * (1.0 - smoothstep(.01, .036, abs(d - ridged + .03))));
            cut = mix(cut, color1, 1.0 - smoothstep(pithRadius * .92, pithRadius + .015, d));
            float local = mod(a + 3.14159 / canalCount, TAU / canalCount) - 3.14159 / canalCount;
            float canal = length(vec2(sin(local) * d, cos(local) * d - stalkRadius * .69));
            cut = mix(cut, color4, 1.0 - smoothstep(.042, .065, canal));
            cut = mix(cut, color1, 1.0 - smoothstep(.029, .049, canal));
            result = mix(result, cut,
                         (1.0 - smoothstep(ridged, ridged + .012, d)) * smoothstep(.013, .071, cell.y));
        """.trimIndent()

        PatternTextureType.LotusSeedheads -> """
            vec2 q = rotatePoint(p, -0.23) * 2.75;
            float earth = layerNoise(q * 5.0);
            result = mix(color1, color2, 0.20 + 0.45 * earth);
            float pedicel = repeatDistance(q.x + 0.21 * sin(q.y * 4.1) + 0.18 * q.y);
            result = mix(result, color3, (1.0 - smoothstep(0.017, 0.035, pedicel)) * 0.69);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    vec2 center = id + 0.13 + 0.74 * hash2(id);
                    float size = headSize * (0.62 + 1.14 * hash(id + 7.0));
                    vec2 local = rotatePoint(q - center, -0.18 + (hash(id + 13.0) - 0.5) * 1.78) / size;
                    vec2 face = local * vec2(1.0, 1.08 + 0.70 * hash(id + 12.0));
                    float edge = length(face) + 0.037 * (noise(face * 7.0) - 0.5);
                    float side = length((face - vec2(0.015, 0.135)) * vec2(1.01, 0.99));
                    if (side < 1.01) {
                        float fibers = 0.5 + 0.5 * cos(face.x * 94.0 + 3.1 * face.y);
                        result = mix(result, mix(color1, color2, 0.11 + 0.30 * fibers),
                                     1.0 - smoothstep(0.97, 1.01, side));
                    }
                    if (edge < 1.015) {
                        vec2 cells = face * (2.26 + 0.45 * hash(id + 8.0)) + id * 7.62;
                        vec4 hole = surfaceCell(cells, 0.67);
                        vec2 v = hole.zw * vec2(1.03, 1.10);
                        float h = length(v);
                        float radius = socketSize * 1.75 * (0.86 + 0.23 * hash(floor(cells - hole.zw)));
                        float ridge = 1.0 - smoothstep(0.0, 0.10, hole.y);
                        float fib = noise(face * 47.0);
                        vec4 disk = mix(color2, color3, 0.33 + 0.25 * (1.0 - edge) + 0.19 * fib);
                        disk = mix(disk, color4,
                                   0.31 * ridge + 0.27 * (1.0 - smoothstep(0.035, 0.079, abs(h - radius))));
                        disk = mix(disk, color1, 1.0 - smoothstep(radius * 0.79, radius, h));
                        float fill = step(hash(floor(cells - hole.zw) + 17.0), seedFill);
                        float seedRad = length((v - vec2(0.015, 0.035)) * vec2(0.92, 1.13));
                        vec4 seedCoat =
                            mix(color1, color2, 0.25 + 0.51 * smoothstep(-0.10, 0.15, -v.x - v.y));
                        disk = mix(disk, seedCoat,
                                   fill * (1.0 - smoothstep(radius * 0.54, radius * 0.76, seedRad)));
                        disk = mix(
                            disk, color4,
                            0.79 * (1.0 - smoothstep(0.014, 0.039, length(v + vec2(.045, .027)))) * fill);
                        disk = mix(disk, color4, 0.50 * smoothstep(.91, .99, edge));
                        result = mix(result, disk, 1.0 - smoothstep(.98, 1.015, edge));
                    }
                }
        """.trimIndent()

        PatternTextureType.DuckweedRafts -> """
            vec2 q = p * 11.0;
            result = mix(color1, color2, .16 + .34 * layerNoise(p * 11.0));
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    vec2 id = floor(q) + vec2(float(i), float(j));
                    float pool = noise(id * .37);
                    if (pool < raftGap * .82)
                        continue;
                    vec2 uv = rotatePoint(q - id - .21 - .58 * hash2(id), hash(id + 4.0) * TAU);
                    for (int n = 0; n < 3; n++) {
                        if (n > 0 && hash(id + float(n) * 12.0) > budding)
                            continue;
                        vec2 leaf = n == 0
                                        ? uv
                                        : rotatePoint(uv - vec2(float(n) * .13 - .02, float(n) * .15 - .35),
                                                      float(n) * 1.8);
                        float r = length(leaf * vec2(1.03, 1.48));
                        float size = frondSize * (n == 0 ? 1.23 : .83);
                        float mask = 1.0 - smoothstep(size, size + .014, r);
                        vec4 frond =
                            mix(color2, color3,
                                .32 + .36 * hash(id + float(n)) + .21 * (1.0 - smoothstep(0.0, size, r)));
                        frond = mix(frond, color4,
                                    (1.0 - smoothstep(.0, .028, abs(leaf.y))) *
                                        (1.0 - smoothstep(.027, .14, abs(leaf.x))) * .66);
                        frond =
                            mix(frond, color4, .17 * (1.0 - smoothstep(.010, .033, abs(r - size + .025))));
                        result = mix(result, frond, mask);
                    }
                }
        """.trimIndent()

        PatternTextureType.MapleBurl -> """
            vec2 q = rotatePoint(p, .18) * 5.2;
            vec4 knot = surfaceCell(q * vec2(.84, 1.13), .86);
            float radius = length(knot.zw * vec2(1.65, .86));
            float lens = exp(-radius * radius / (eyeSize * eyeSize * 2.9));
            float bend = grainCurl * .19 * lens * (.22 + abs(knot.w) * 3.0);
            float fiber = q.x / 5.2 + bend + .021 * sin(q.y * 2.0) + .022 * noise(q * vec2(2.7, .81));
            float ridge = .5 + .5 * cos(TAU * fiber / grainPitch);
            result = mix(color3, color4, .22 + .42 * noise(q * vec2(19.0, 4.0)) + .21 * ridge);
            result = mix(result, color2, .49 * pow(ridge, 3.0));
            result = mix(result, color1, lens * .72);
            result = mix(result, color2, .75 * (1.0 - smoothstep(.01, .045, abs(radius - eyeSize * .66))));
        """.trimIndent()

        PatternTextureType.MusselByssus -> """
            vec2 q = p * 3.8;
            result = mix(color1, color2, .22 + .53 * layerNoise(q * 6.0));
            vec4 colony = surfaceCell(q, .64);
            vec2 id = floor(q - colony.zw);
            float turn = hash(id + 3.0) * TAU;
            vec2 origin = -.13 * vec2(cos(turn), sin(turn));
            for (int k = 0; k < 13; k++) {
                if (float(k) >= threadCount)
                    break;
                float f = float(k), a = turn + anchorSpread * (hash(id + f + 7.0) - .5) * 2.65;
                vec2 tip = origin + (.27 + .28 * hash(id + f + 21.0)) * vec2(cos(a), sin(a));
                vec2 mid = mix(origin, tip, .54) + .035 * vec2(-sin(a), cos(a));
                float line = min(segment(colony.zw, origin, mid), segment(colony.zw, mid, tip));
                result = mix(result, color3, (1.0 - smoothstep(.005, .017, line)) * .93);
                float cap = length(rotatePoint(colony.zw - tip, -a) * vec2(.76, 1.66));
                result = mix(result, color4, 1.0 - smoothstep(plaqueSize * .66, plaqueSize, cap));
            }
        """.trimIndent()

        PatternTextureType.SandDollarBed -> """
            vec2 q = rotatePoint(p, -0.48) * vec2(3.55, 6.7);
            float chips = layerNoise(p * 77.0);
            result = mix(color1, color2, .35 + .52 * chips);
            for (int j = -1; j <= 1; j++)
                for (int i = -1; i <= 1; i++) {
                    float row = floor(q.y) + float(j);
                    vec2 id = vec2(floor(q.x - 0.5 * mod(row, 2.0)) + float(i), row);
                    vec2 center = id + vec2(0.5 + 0.5 * mod(row, 2.0), 0.52) + 0.28 * (hash2(id) - .5);
                    vec2 uv = rotatePoint((q - center) * vec2(1.0, 0.92), (hash(id + 5.0) - .5) * .54);
                    float r0 = testRadius * (1.12 + .49 * hash(id + 2.0));
                    vec2 v = uv / r0;
                    float r = length(v) * (1.0 + .028 * cos(5.0 * safeAngle(v) + hash(id) * 2.0));
                    float shadow = 1.0 - smoothstep(0.94, 1.035, length((uv - vec2(.035, .074)) / r0));
                    result = mix(result, color1, shadow * .64);
                    if (r < 1.025) {
                        float rot = (hash(id + 30.0) - .5) * 2.2;
                        float a = safeAngle(v) + rot;
                        float lobe = mod(a + .628319, 1.256637) - .628319;
                        float lb = petalReach * 2.27;
                        vec2 z = vec2(cos(lobe), sin(lobe)) * r;
                        float t = z.x / lb;
                        float leafWidth = .136 * pow(max(0.0, sin(3.14159 * clamp(t, 0., 1.))), .70);
                        vec2 pp = vec2(repeatDistance(t * poreRows) * lb / poreRows, abs(z.y) - leafWidth);
                        float pores = 1.0 - smoothstep(.013, .030, length(pp));
                        float print =
                            (1.0 - smoothstep(.012, .031, abs(abs(z.y) - leafWidth))) * .72 + pores * .67;
                        vec4 shell = mix(color3, color4,
                                         .41 + .24 * hash(id + 8.0) + .15 * noise(v * 62.0) - .16 * v.y);
                        shell = mix(shell, color2, min(1.0, print) * step(.035, t) * step(t, 1.0));
                        float keyhole = length((z - vec2(.765, .0)) * vec2(.62, 1.70));
                        shell = mix(shell, color1, (1.0 - smoothstep(.033, .060, keyhole)) * .87);
                        shell = mix(shell, color2, smoothstep(.89, 1.0, r) * .64);
                        result = mix(result, shell, 1.0 - smoothstep(.979, 1.025, r));
                    }
                }
        """.trimIndent()

        PatternTextureType.ScuteGrowthRings -> """
            vec2 q = p * 5.6 * vec2(.86, 1.17);
            vec4 cell = surfaceCell(q, .37);
            vec2 id = floor(q - cell.zw);
            float normalized = cell.x / (cell.x + .61 * cell.y + .0001);
            float ring = repeatDistance(normalized * growthBands + .12 * noise(q * 5.0));
            result = mix(color2, color3, .38 + .36 * hash(id) + .18 * layerNoise(q * 8.0));
            result = mix(result, color4, (1.0 - smoothstep(.011, .097, ring)) * (.35 + .34 * normalized));
            result.rgb *= .78 + scuteDome * .35 * (1.0 - normalized) + .15 * noise(q * 31.0);
            result = mix(color1, result, smoothstep(sutureWidth * .46, sutureWidth * 1.35, cell.y));
        """.trimIndent()

        PatternTextureType.CuttlefishMantle -> """
            vec2 q = rotatePoint(p, -.32);
            float plume = abs(sin(q.x * 4.2 + q.y * .8));
            float stripes = q.y / barPitch +
                            ruffle * (.65 * sin(11.0 * q.x) + .49 * sin(19.0 * q.x + q.y * 4.0)) +
                            .42 * layerNoise(p * 13.0);
            float t = repeatDistance(stripes);
            float bar = 1.0 - smoothstep(.12, .27 + .09 * plume, t);
            result = mix(color1, color2, .35 + .4 * layerNoise(p * 25.0));
            result = mix(result, mix(color3, color4, .24 + .47 * plume), bar);
            vec4 dots = surfaceCell(p * 82.0, .86);
            float spots = (1.0 - smoothstep(.095, .24, dots.x)) *
                          smoothstep(.21, .72, hash(floor(p * 82.0 - dots.zw)));
            result = mix(result, color1, speckles * spots);
        """.trimIndent()

        PatternTextureType.LimpetRadula -> """
            vec2 q = rotatePoint(p, .28) / toothSpacing;
            q.x += .48 * mod(floor(q.y), 2.0);
            vec2 uv = fract(q) - .5;
            float phase = fract(q.y);
            result = mix(color1, color2, .29 + .31 * layerNoise(p * 31.0));
            result = mix(result, color3, .31 * (1.0 - smoothstep(.015, .045, abs(phase - .16))));
            float t = (uv.y + .31) / .68;
            float bend = .04 + hookReach * .39 * t * t;
            float radius = .13 * (1.0 - clamp(t, 0.0, 1.0)) + .018;
            float tooth = (1.0 - smoothstep(radius, radius + .018, abs(abs(uv.x) - .12 - bend))) *
                          smoothstep(-.02, .04, t) * (1.0 - smoothstep(.92, 1.02, t));
            vec4 crown = mix(color3, color4, .50 + .27 * noise(q * 19.0));
            crown = mix(crown, color1, tipMineral * smoothstep(.61, .94, t));
            result = mix(result, crown, tooth);
        """.trimIndent()

        PatternTextureType.CornerCubeReflector -> """
            vec2 lattice = vec2(p.x - p.y * 0.57735, p.y * 1.1547) + vec2(phaseX, phaseY);
            vec2 uv = fract(lattice);
            float invert = step(1.0, uv.x + uv.y);
            if (invert > 0.5)
                uv = 1.0 - uv;
            vec3 bary = vec3(uv.x, uv.y, 1.0 - uv.x - uv.y);
            float face = bary.x < bary.y && bary.x < bary.z ? 0.0 : (bary.y < bary.z ? 1.0 : 2.0);
            float closest = min(bary.x, min(bary.y, bary.z));
            float farthest = max(bary.x, max(bary.y, bary.z));
            float middle = 1.0 - closest - farthest;
            float split = (middle - closest) * 0.46;
            float shine = 0.5 + 0.5 * cos(face * TAU / 3.0 + radians(lightAzimuth) + invert * 3.14159);
            vec4 surface = mix(color2, color3, 0.14 + facetRelief * shine * 0.71);
            surface = mix(surface, color4, pow(shine, 4.0) * facetRelief * (0.58 + 0.25 * farthest));
            float boundary = coverage(closest * 0.866, reflectorSeam * 0.56);
            float ridge = coverage(split, reflectorSeam * 0.40);
            result = mix(surface, color1, max(boundary * 0.91, ridge * 0.69));
        """.trimIndent()

        PatternTextureType.MicroficheFrames -> """
            vec2 grid = p * vec2(0.7, 1.0);
            vec2 cell = floor(grid), local = fract(grid);
            float paper = min(min(local.x, 1.0 - local.x), min(local.y, 1.0 - local.y));
            float frame = smoothstep(filmMargin, filmMargin + pixelWidth, paper);
            float scratch = smoothstep(0.80, 0.91, noise(vec2(p.x * 100.0, p.y * 2.8)));
            result = mix(color1, color2, (0.12 + 0.35 * noise(p * 0.7) + 0.16 * scratch) * microFog);
            vec4 emulsion = mix(color2, color3, 0.18 + 0.48 * noise(cell + 7.0));
            result = mix(result, emulsion, frame);
            vec2 sheet = (local - filmMargin) / (1.0 - 2.0 * filmMargin);
            float row = floor(sheet.y * 22.0);
            float end = mix(0.49, 0.95, hash(cell * 47.0 + row));
            float column = fract(sheet.x * 2.0);
            float columnMask = smoothstep(0.07, 0.10, column) * (1.0 - smoothstep(end - 0.02, end, column));
            float gaps = step(0.18, hash(vec2(floor(column * 33.0), row) + cell));
            float rules = coverage(abs(fract(sheet.y * 22.0) - 0.48) / 22.0, 0.009) * columnMask;
            float header =
                coverage(abs(sheet.y - 0.085), 0.015) * step(0.10, sheet.x) * step(sheet.x, 0.68);
            float ink = max(header, rules * gaps * step(0.18, sheet.y) * step(sheet.y, 0.92) *
                                        step(hash(cell + row * 9.1), lineFill));
            result = mix(result, color4, ink * frame * 0.86);
        """.trimIndent()

        PatternTextureType.AcousticSlotPanel -> """
            vec2 grid = p * vec2(1.0, 0.60);
            grid.y += 0.5 * mod(floor(grid.x), 2.0);
            vec2 q = fract(grid) - 0.5;
            float groove = 10.0;
            for (int i = 0; i < 3; i++) {
                float x = (float(i) - 1.0) * 0.32;
                float halfLength = slotLength * (i == 1 ? 0.50 : 0.41);
                groove = min(groove, segment(q, vec2(x, -halfLength), vec2(x, halfLength)));
            }
            float fiber = noise(vec2(p.x * 53.0, p.y * 1.25));
            result = mix(color2, color3, 0.32 + panelGrain * (fiber - 0.5) * 0.76 + 0.15 * noise(p * 3.0));
            float lip = coverage(groove, slotWidth + 0.023);
            result = mix(result, color4, lip * 0.72);
            result = mix(result, color1, coverage(groove, slotWidth));
        """.trimIndent()

        PatternTextureType.BearingRaceway -> """
            vec2 q = fract(p + vec2(phaseX, phaseY)) - 0.5;
            float lane = abs(q.y), radial = length(q * vec2(1.0, 1.05));
            float rail = 0.5 + 0.5 * cos((lane - 0.36) * 32.0);
            result = mix(color1, color2, 0.17 + rail * 0.65);
            result = mix(result, color4, smoothstep(0.94, 1.0, rail) * steelPolish * 0.60);
            float cage = coverage(abs(radial - ballRadius - cageWidth * 0.60), cageWidth * 0.63);
            cage = max(cage, coverage(abs(q.y), cageWidth * 0.46));
            result = mix(result, color3, cage);
            float body = coverage(radial, ballRadius);
            vec2 uv = q / max(ballRadius, 0.01);
            float z = sqrt(max(0.0, 1.0 - dot(uv, uv)));
            vec3 normal = vec3(uv, z);
            float lighting = clamp(dot(normal, normalize(vec3(-0.5, -0.7, 0.8))), 0.0, 1.0);
            vec4 steel = mix(color1, color2, 0.30 + 0.67 * lighting);
            steel = mix(
                steel, color4,
                steelPolish * (0.73 * pow(lighting, 2.4) + 0.47 * exp(-pow((uv.y - 0.33) / 0.16, 2.0))));
            result = mix(result, steel, body);
        """.trimIndent()

        PatternTextureType.PinFinHeatsink -> """
            vec2 grid = vec2(p.x + 0.22 * p.y, p.y - 0.29 * p.x) + vec2(phaseX, phaseY * 2.0);
            float row = floor(grid.y);
            vec2 q = fract(vec2(grid.x + mod(row, 2.0) * 0.5, grid.y)) - 0.5;
            float topY = -pinHeight * 0.54, bottomY = pinHeight * 0.54;
            float diamond = abs(q.x) + abs(q.y - topY) * 1.75 - pinRadius;
            float wall = abs(q.x) + abs(q.y - clamp(q.y, topY, bottomY)) * 1.75 - pinRadius;
            vec2 projected = q - vec2(0.07, 0.09);
            float shadow =
                abs(projected.x) + abs(projected.y - clamp(projected.y, topY, bottomY)) * 1.75 - pinRadius;
            result = mix(color2, color1, 0.22 + 0.62 * coverage(shadow, 0.027));
            vec4 side = mix(color1, color2, 0.25 + 0.65 * step(0.0, q.x));
            result = mix(result, side, coverage(wall, 0.0));
            vec4 top =
                mix(color3, color4, 0.23 + 0.27 * clamp((q.x + pinRadius) / (2.0 * pinRadius), 0.0, 1.0));
            top = mix(top, color4, coverage(abs(diamond), topBevel) * 0.65);
            result = mix(result, top, coverage(diamond, 0.0));
        """.trimIndent()

        PatternTextureType.TreadPlate -> """
            vec2 grid = p + 2.0 * vec2(phaseX, phaseY);
            vec2 tile = floor(grid), q = fract(grid) - 0.5;
            q = rotatePoint(q, mix(-0.7854, 0.7854, mod(tile.x + tile.y, 2.0)));
            float rnd = 0.5 * noise(p * 33.0) + 0.5 * noise(p * 88.0);
            result = mix(color1, color2, 0.44 + rnd * 0.33);
            for (int i = 0; i < 3; i++) {
                float dx = q.x + (float(i) - 1.0) * 0.178;
                float t = q.y / (lugLength * (i == 1 ? 0.50 : 0.40));
                float width = lugBreadth * (1.0 - t * t);
                float d = abs(dx) - max(width, 0.0);
                float mask = coverage(d, 0.0) * (1.0 - smoothstep(0.95, 1.0, abs(t)));
                float cross = clamp(dx / max(width, 0.007), -1.0, 1.0);
                vec4 raised = mix(color2, color3, 0.58 - 0.30 * cross + 0.08 * rnd);
                raised = mix(raised, color4, edgePolish * exp(-pow((cross + 0.48) * 3.0, 2.0)));
                result = mix(result, raised, mask);
            }
        """.trimIndent()

        PatternTextureType.SplitFlapMatrix -> """
            vec2 grid = p * vec2(1.18, 0.81), cell = floor(grid), q = fract(grid) - 0.5;
            vec2 panel = abs(q) - vec2(0.469, 0.463);
            float card = coverage(max(panel.x, panel.y), 0.0);
            float patina = noise(cell + 19.0);
            result = mix(color1, mix(color1, color2, 0.38 + 0.21 * patina + 0.12 * step(q.y, 0.0)), card);
            float digit = floor(10.0 * hash(cell + 2.7));
            float code = 119.0;
            if (digit < 0.5)
                code = 63.0;
            else if (digit < 1.5)
                code = 6.0;
            else if (digit < 2.5)
                code = 91.0;
            else if (digit < 3.5)
                code = 79.0;
            else if (digit < 4.5)
                code = 102.0;
            else if (digit < 5.5)
                code = 109.0;
            else if (digit < 6.5)
                code = 125.0;
            else if (digit < 7.5)
                code = 7.0;
            else if (digit < 8.5)
                code = 127.0;
            else
                code = 111.0;
            float ink = 0.0;
            for (int i = 0; i < 7; i++) {
                float n = float(i);
                vec2 v = vec2(0.0), w = vec2(0.0);
                if (i == 0 || i == 3 || i == 6) {
                    float y = i == 0 ? -0.31 : (i == 3 ? 0.31 : 0.0);
                    v = vec2(-0.125, y);
                    w = vec2(0.125, y);
                } else {
                    float x = i == 1 || i == 2 ? 0.155 : -0.155;
                    float y = i == 1 || i == 5 ? -0.155 : 0.155;
                    v = vec2(x, y - 0.111);
                    w = vec2(x, y + 0.111);
                }
                ink = max(ink, coverage(segment(q, v, w), segmentStroke) * mod(floor(code / exp2(n)), 2.0));
            }
            result = mix(result, mix(color3, color4, patina), ink * step(blankFraction, hash(cell + 4.0)));
            result = mix(result, color1, coverage(abs(q.y), flapGap));
        """.trimIndent()

        PatternTextureType.WireRopeSection -> """
            vec2 grid = p * 1.18, base = floor(grid);
            float nearest = 9.0, tone = 0.0, spacing = 0.091;
            vec2 local = vec2(0.0);
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 id = base + vec2(float(x), float(y));
                    vec2 center = id + vec2(0.5 + 0.5 * mod(id.y, 2.0), 0.5);
                    vec2 d = grid - center;
                    if (dot(d, d) < nearest) {
                        nearest = dot(d, d);
                        local = d;
                        tone = hash(id);
                    }
                }
            result = mix(color1, color2, 0.13 + 0.2 * noise(p * 41.0));
            float sheath = coverage(abs(length(local) - 0.446), 0.018);
            result = mix(result, color3, sheath * 0.74);
            for (int i = 0; i < 7; i++) {
                float angle = float(i) * TAU / 6.0;
                vec2 sc = (i == 6 ? vec2(0.0) : vec2(cos(angle), sin(angle)) * 0.289);
                vec2 strand = local - sc;
                float group = coverage(length(strand), 0.147);
                result = mix(result, color1, group);
                for (int j = 0; j < 7; j++) {
                    float spin = float(j) * TAU / 6.0 + strandTwist * (tone * 2.6 + float(i) * 0.63);
                    vec2 w = strand - (j == 6 ? vec2(0.0) : vec2(cos(spin), sin(spin)) * spacing);
                    float r = (j == 6 ? wireCore * 0.5 : spacing * 0.5) * strandPacking;
                    float disc = coverage(length(w), r);
                    float hl =
                        clamp(0.48 - dot(w, vec2(3.6, 5.0)) + 0.16 * hash(vec2(float(i), float(j)) + tone),
                              0.0, 1.0);
                    vec4 metal = mix(color2, color3, hl);
                    metal = mix(metal, color4, coverage(abs(length(w) - r * 0.8), r * 0.095) * 0.65);
                    result = mix(result, metal, disc);
                }
            }
        """.trimIndent()

        PatternTextureType.KnurledInsert -> """
            vec2 g = vec2(p.x + knurlSlant * p.y, p.x - knurlSlant * p.y) + vec2(phaseX, phaseY);
            vec2 tooth = abs(fract(g) - 0.5) * 2.0;
            float h = min(tooth.x, tooth.y), cap = 1.0 - knurlFlat;
            float level = smoothstep(0.0, cap, h);
            float face = tooth.x < tooth.y ? sign(fract(g.x) - 0.5) : sign(fract(g.y) - 0.5) * 0.47;
            result = mix(color1, color2, 1.0 - knurlOxide * (1.0 - level));
            result = mix(result, mix(color2, color3, 0.54 - 0.35 * face), smoothstep(0.025, 0.20, level));
            result = mix(result, color4, 0.22 * level + 0.57 * smoothstep(cap - 0.03, cap + 0.05, h));
        """.trimIndent()

        PatternTextureType.BallGridArray -> """
            vec2 grid = p, cell = floor(grid), q = fract(grid) - 0.5;
            float disc = length(q), r = 0.5 * solderDiameter;
            float occupied = step(hash(cell + 4.0), occupiedPads);
            float stub = segment(q, vec2(0.0), vec2(0.50, 0.28 * sign(hash(cell) - 0.5)));
            result = mix(color1, color2, 0.20 + 0.38 * noise(p * 18.0));
            result = mix(result, color2, coverage(stub, 0.023));
            result = mix(result, color3, coverage(disc, r + padCollar));
            result = mix(result, color1, coverage(disc, r * 0.63) * (1.0 - occupied) * 0.47);
            vec2 uv = q / r;
            float light =
                clamp(-uv.x * 0.33 - uv.y * 0.41 + sqrt(max(0.0, 1.0 - dot(uv, uv))) * 0.61, 0.0, 1.0);
            vec4 metal = mix(color1, color4, 0.26 + 0.65 * light);
            metal = mix(metal, color4, exp(-dot(uv + vec2(0.36, 0.39), uv + vec2(0.36, 0.39)) * 23.0));
            result = mix(result, metal, coverage(disc, r) * occupied);
        """.trimIndent()

        PatternTextureType.PiezoComb -> """
            vec2 q = fract(p + vec2(phaseX, phaseY));
            float lane = floor(q.y * 2.0 * fingerPairs);
            float dist = abs(fract(q.y * 2.0 * fingerPairs) - 0.5) / (2.0 * fingerPairs);
            float u = mix(q.x, 1.0 - q.x, mod(lane, 2.0));
            float bus = coverage(min(abs(q.x - 0.069), abs(q.x - 0.931)), 0.032);
            float metal = coverage(dist, fingerWidth / (4.0 * fingerPairs)) * step(0.070, u) *
                          (1.0 - smoothstep(fingerReach, fingerReach + pixelWidth, u));
            result = mix(color1, color2, 0.27 + 0.21 * noise(p * 4.0) + 0.10 * noise(p * 32.0));
            vec4 electrode = mix(color3, color4, 0.17 + 0.52 * (1.0 - q.y) + 0.12 * cos(q.x * TAU));
            result = mix(result, electrode, max(metal, bus));
        """.trimIndent()

        PatternTextureType.PolarizerMosaic -> """
            vec2 g = (p + vec2(phaseX, phaseY)) * 2.0;
            vec2 tile = mod(floor(g), 2.0), q = fract(g) - 0.5;
            float orientation = (tile.x + 2.0 * tile.y) * 0.785398;
            vec2 axis = vec2(cos(orientation), sin(orientation));
            float stripe = cos(TAU * lamellaCount * dot(q, axis));
            float visible = clamp(1.0 - pixelWidth * lamellaCount * 2.2, 0.0, 1.0);
            float energy = cos(orientation - radians(analyzerAngle));
            energy = polarizerLeak + (1.0 - polarizerLeak) * energy * energy;
            vec4 film = mix(color2, color3, 0.5 + 0.45 * dot(axis, vec2(0.65, -0.6)));
            film = mix(color1, film, energy);
            film = mix(film, color4, (0.5 + 0.5 * visible * stripe) * energy * 0.72);
            float seam = coverage(min(0.5 - abs(q.x), 0.5 - abs(q.y)), 0.03);
            result = mix(film, color1, seam);
        """.trimIndent()

        PatternTextureType.PhosphorTriads -> """
            vec2 g = p * vec2(1.0, 1.45) + vec2(2.0 * phaseX, phaseY);
            float column = floor(g.x * 3.0);
            float channel = mod(column, 3.0);
            g.y += mod(column, 2.0) * 0.5;
            vec2 q = vec2(fract(g.x * 3.0) - 0.5, fract(g.y) - 0.5);
            vec2 edge = abs(q) - vec2(0.45 - maskRatio * 0.29, phosphorHeight * 0.5);
            float d = length(max(edge + 0.125, vec2(0.0))) + min(max(edge.x + 0.125, edge.y + 0.125), 0.0) -
                      0.125;
            vec4 phosphor = channel < 0.5 ? color2 : (channel < 1.5 ? color3 : color4);
            float halo = exp(-max(0.0, d) * 6.5) * crtBloom * 0.31;
            float lit = coverage(d, 0.0) * (0.48 + 0.32 * (1.0 - q.y * q.y * 2.5));
            result = mix(color1, phosphor, min(1.0, lit + halo));
        """.trimIndent()

        PatternTextureType.AnamorphicStreaks -> """
            vec2 g = p * vec2(0.77, 1.35), root = floor(g);
            result = mix(color1, color2, 0.05 + noise(g * 0.43) * 0.17);
            float streak = 0.0, point = 0.0, ghost = 0.0;
            for (int y = -1; y <= 1; y++)
                for (int x = -2; x <= 2; x++) {
                    vec2 id = root + vec2(float(x), float(y));
                    vec2 d = g - id - 0.13 - 0.74 * hash2(id);
                    float power = step(hash(id + 17.0), glintCount) * (0.45 + 0.55 * hash(id + 29.0));
                    streak += power * exp(-abs(d.x) * 2.3 / streakReach - abs(d.y) * 32.0);
                    point += power * exp(-dot(d, d) * 280.0);
                    vec2 g1 = (d - vec2(ghostOffset, 0.018)) * vec2(1.38, 0.9);
                    ghost += power * (0.35 * exp(-dot(g1, g1) * 43.0) +
                                      0.85 * exp(-pow((length(g1) - 0.102) / 0.027, 2.0)));
                }
            result = mix(result, color2, clamp(streak, 0.0, 0.92));
            result = mix(result, color3, clamp(ghost + 0.77 * streak, 0.0, 0.92));
            result = mix(result, color4, clamp(point + 0.18 * streak, 0.0, 1.0));
        """.trimIndent()

        PatternTextureType.DichroicSplitter -> """
            vec2 a = rotatePoint(p, 0.21), b = rotatePoint(p, radians(paneTilt) + 0.21);
            float ua = fract(a.x + 0.16 * hash(vec2(floor(a.x), 3.1))),
                  ub = fract(b.x + 0.12 * hash(vec2(floor(b.x), 6.0)));
            float da = abs(ua - 0.5) - glassFraction * 0.5, db = abs(ub - 0.5) - glassFraction * 0.47;
            float m = coverage(da, 0.0), s = coverage(db, 0.0);
            float shimmer = 0.5 + 0.5 * cos(TAU * (0.36 * a.y + 0.53 * ua + 0.32 * noise(p * 0.8)));
            result = mix(color1, color2, 0.10 + 0.13 * noise(p * 2.5));
            result = mix(result, mix(color2, color4, 0.42 * shimmer), m * (0.28 + 0.58 * coatingMix));
            result = mix(result, mix(color3, color4, 0.64 * m * shimmer), s * (0.30 + 0.56 * coatingMix));
            float polished = max(coverage(abs(da), 0.013), coverage(abs(db), 0.012));
            result = mix(result, color4, 0.74 * polished);
        """.trimIndent()

        PatternTextureType.TalbotCarpet -> """
            vec2 q = p + vec2(phaseX, phaseY);
            vec2 amplitude = vec2(slitFraction, 0.0);
            for (int j = 1; j <= 11; j++) {
                float m = float(j);
                if (m > modeCount)
                    break;
                float weight = 2.0 * sin(3.141593 * m * slitFraction) / (3.141593 * m);
                float travel = TAU * q.y * m * m;
                float bandLimit = 1.0 - smoothstep(0.18, 0.64, pixelWidth * m * m);
                float bandwidth = bandLimit * exp(-1.4 * m * m / (modeCount * modeCount));
                amplitude += vec2(cos(travel), sin(travel)) * weight * cos(TAU * m * q.x) * bandwidth;
            }
            float light = dot(amplitude, amplitude),
                  brilliance = 1.0 - exp(-light * (2.0 + carpetContrast * 4.0));
            result = mix(color1, color2, smoothstep(0.0, 0.43, brilliance));
            result = mix(result, color3, smoothstep(0.31, 0.76, brilliance));
            result = mix(result, color4, smoothstep(0.69, 0.98, brilliance));
        """.trimIndent()

        PatternTextureType.ConoscopicCrosses -> """
            vec2 root = floor(p);
            float nearest = 12.0, second = 12.0, axis = 0.0;
            vec2 q = vec2(0.0);
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 id = root + vec2(float(x), float(y));
                    vec2 d = p - id - 0.5 - (hash2(id) - 0.5) * opticEccentricity;
                    float length2 = dot(d, d);
                    second = min(second, max(nearest, length2));
                    if (length2 < nearest) {
                        nearest = length2;
                        q = d;
                        axis = hash(id + 11.0) * TAU;
                    }
                }
            vec2 r = rotatePoint(q, axis);
            float retardation = sqrt(dot(r * vec2(1.0, 1.27), r * vec2(1.0, 1.27))) * isochromes;
            float crossLight =
                smoothstep(extinctionWidth * 0.12, extinctionWidth, abs(sin(2.0 * safeAngle(r))));
            vec4 ring = mix(color2, color3, 0.5 + 0.5 * cos(TAU * retardation));
            ring = mix(ring, color4, 0.58 * (0.5 + 0.5 * cos(TAU * retardation * 0.67 + 0.9)));
            result =
                mix(color1, ring, crossLight * (0.53 + 0.47 * smoothstep(0.0, 0.10, second - nearest)));
        """.trimIndent()

        PatternTextureType.SpeckleShear -> """
            vec2 q = p * (1.42 - grainRate);
            vec2 wave = vec2(0.0);
            for (int j = 0; j < 7; j++) {
                float i = float(j), ang = TAU * hash(vec2(i, 3.0));
                float w = TAU * (dot(q, vec2(cos(ang), sin(ang))) * (3.1 + 4.3 * hash(vec2(i, 8.0))) +
                                 hash(vec2(i, 9.0)));
                wave += vec2(cos(w), sin(w));
            }
            float intensity = 1.0 - exp(-dot(wave, wave) * 0.13);
            float phase =
                safeAngle(wave) + TAU * (p.x * (0.7 + shearRate * 1.3) + shearRate * noise(p * 1.7));
            float fringe = 0.5 + 0.5 * cos(phase);
            result = mix(color1, color2, intensity * 0.88);
            result = mix(result, color3, intensity * fringe * fringeGain);
            result = mix(result, color4, smoothstep(0.68, 0.99, intensity * fringe) * 0.87);
        """.trimIndent()

        PatternTextureType.PythagoreanTiling -> """
            float smallSide = insertRatio;
            vec2 basisX = vec2(1.0, smallSide), basisY = vec2(-smallSide, 1.0);
            float length2 = 1.0 + smallSide * smallSide;
            vec2 ij = floor(vec2(dot(p, basisX), dot(p, basisY)) / length2);
            float signedDistance = 8.0;
            vec4 stone = color2;
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 id = ij + vec2(float(x), float(y));
                    vec2 center = id.x * basisX + id.y * basisY;
                    vec2 large = abs(p - center);
                    vec2 inset = abs(p - center - vec2(0.5 + 0.5 * smallSide, -0.5 + 0.5 * smallSide));
                    float a = max(large.x, large.y) - 0.5;
                    float b = max(inset.x, inset.y) - smallSide * 0.5;
                    if (min(a, b) < signedDistance) {
                        signedDistance = min(a, b);
                        stone = a < b ? mix(color2, color3, hash(id) * 0.31)
                                      : mix(color3, color4, 0.30 + 0.34 * hash(id + 9.0));
                    }
                }
            float edge = coverage(signedDistance, -stoneJoint * 0.5);
            stone = mix(stone, color4, mineralGrain * (0.11 + 0.36 * noise(p * 29.0)));
            result = mix(color1, stone, edge);
        """.trimIndent()

        PatternTextureType.MiuraFolds -> """
            vec2 grid = p + 2.0 * vec2(phaseX, phaseY);
            float row = floor(grid.y), fy = fract(grid.y);
            float zig = mix(fy, 1.0 - fy, mod(row, 2.0));
            float col = grid.x - foldSkew * zig, side = floor(col);
            float checker = mod(side + row, 2.0);
            float slope = mix(fract(col), 1.0 - fract(col), mod(row, 2.0));
            vec4 face = mix(color2, color3, checker);
            face = mix(face, color4, (0.12 + 0.42 * slope) * foldLight);
            face = mix(face, color1, foldLight * (0.05 + 0.23 * mod(row, 2.0)));
            float crease = min(min(fy, 1.0 - fy), min(fract(col), 1.0 - fract(col)));
            result = mix(face, color1, coverage(crease, creaseWidth) * 0.68);
        """.trimIndent()

        PatternTextureType.DovetailMesh -> """
            vec2 g = p + vec2(phaseX, phaseY), q = fract(g) - 0.5;
            float along = abs(q.x);
            float flare = mix(0.16, 0.16 + tailFlare, clamp(0.5 + 0.5 * q.y / tailDepth, 0.0, 1.0));
            float plank = step(q.y, -tailDepth);
            plank = max(plank, step(q.y, tailDepth) * step(along, flare));
            float seam = abs(along - flare);
            if (q.y < -tailDepth || q.y > tailDepth)
                seam = 8.0;
            if (along < 0.16 + tailFlare)
                seam = min(seam, abs(q.y - tailDepth));
            if (along > 0.16)
                seam = min(seam, abs(q.y + tailDepth));
            seam = min(seam, 0.5 - abs(q.y));
            float grain = mix(noise(p * vec2(2.3, 64.0)), noise(p * vec2(72.0, 2.8)), plank);
            vec4 board = mix(color2, color3, plank);
            board = mix(board, color4, 0.11 + 0.26 * grain);
            result = mix(board, color1, coverage(seam, joinClearance));
        """.trimIndent()

        PatternTextureType.ReuleauxTripods -> """
            vec2 g = p + vec2(phaseX, phaseY * 2.0);
            float row = floor(g.y);
            g.x += 0.5 * mod(row, 2.0);
            vec2 q = rotatePoint(fract(g) - 0.5, reuleauxSpin + mod(row, 2.0) * 3.14159);
            float r = reuleauxSpan / 1.73205;
            vec2 a = vec2(0.0, -r), b = vec2(0.866025 * r, 0.5 * r), c = vec2(-0.866025 * r, 0.5 * r);
            float sd = max(length(q - a), max(length(q - b), length(q - c))) - reuleauxSpan;
            result = mix(color1, color2, 0.19 + 0.23 * cos(TAU * g.x) * cos(TAU * g.y));
            result = mix(result, color3, coverage(sd, 0.0));
            result = mix(result, color2, coverage(sd, -reuleauxInset));
            result = mix(result, color4, coverage(abs(sd + reuleauxInset * 2.2), 0.013) * 0.85);
        """.trimIndent()

        PatternTextureType.HingedSquares -> """
            vec2 g = p + 2.0 * vec2(phaseX, phaseY), id = floor(g), q = fract(g) - 0.5;
            float parity = mod(id.x + id.y, 2.0), alpha = mix(-hingeOpening, hingeOpening, parity);
            float h = 0.5 / (cos(alpha) + abs(sin(alpha)));
            vec2 v = rotatePoint(q, alpha), side = abs(v) - h;
            float plate = max(side.x, side.y);
            vec4 alloy = mix(color2, color3, parity);
            alloy = mix(alloy, color4, 0.13 + 0.15 * (v.x - v.y + 0.6));
            alloy = mix(alloy, color1, coverage(abs(plate + plateLip * 0.5), plateLip * 0.5) * 0.45);
            result = mix(color1, alloy, coverage(plate, 0.0));
            vec2 joint = abs(v) - h;
            result = mix(result, color4, coverage(length(joint), hingeRadius));
        """.trimIndent()

        PatternTextureType.OctagonCabochons -> """
            vec2 q = fract(p) - 0.5;
            float corners = 1.0 - abs(q.x) - abs(q.y);
            float grout = min(0.5 - max(abs(q.x), abs(q.y)), abs(corners - cabochonSpan) * 0.7071);
            float streak = sin((p.x - p.y) * 26.0 + 6.0 * noise(p * 2.8) + 2.0 * noise(p * 8.0));
            float vein = smoothstep(0.82, 0.97, streak) * marbleVeins;
            vec4 slab = mix(color3, color4, 0.10 + 0.38 * vein + 0.15 * noise(p * 21.0));
            vec4 pin = mix(color1, color2, 0.52 + 0.36 * noise(p * 13.0));
            float rim = coverage(abs(corners - cabochonSpan * 0.72), borderGauge * 0.56);
            pin = mix(pin, color4, rim);
            result =
                mix(pin, slab, smoothstep(cabochonSpan - pixelWidth, cabochonSpan + pixelWidth, corners));
            float actualJoint = corners < cabochonSpan ? abs(corners - cabochonSpan) * 0.7071 : grout;
            result = mix(result, color1, coverage(actualJoint, 0.010));
        """.trimIndent()

        PatternTextureType.PentominoMosaic -> """
            vec2 g = p * 2.4, tile = floor(g), uv = fract(g);
            vec2 nearSide = step(vec2(0.5), uv) * 2.0 - 1.0;
            float central = 0.0, dist = 1.0, variant = 0.0;
            for (int k = 0; k < 3; k++) {
                vec2 u = tile;
                if (k == 1)
                    u.x += nearSide.x;
                if (k == 2)
                    u.y += nearSide.y;
                vec2 board = floor(u / vec2(5.0, 6.0)), v = mod(u, vec2(5.0, 6.0));
                float selector = hash(board + 33.0);
                if (hash(board + 12.0) < 0.5)
                    v.x = 4.0 - v.x;
                if (hash(board + 18.0) < 0.5)
                    v.y = 5.0 - v.y;
                float row = v.y, rowCode = 0.0;
                if (row < 0.5)
                    rowCode = selector < 0.333 ? 4672.0 : (selector < 0.667 ? 9288.0 : 4680.0);
                else if (row < 1.5)
                    rowCode = selector < 0.333 ? 4098.0 : (selector < 0.667 ? 8776.0 : 5248.0);
                else if (row < 2.5)
                    rowCode = selector < 0.333 ? 5778.0 : (selector < 0.667 ? 9408.0 : 13968.0);
                else if (row < 3.5)
                    rowCode = selector < 0.333 ? 14100.0 : (selector < 0.667 ? 14020.0 : 18128.0);
                else if (row < 4.5)
                    rowCode = selector < 0.333 ? 14116.0 : (selector < 0.667 ? 22308.0 : 18285.0);
                else if (row < 5.5)
                    rowCode = selector < 0.333 ? 23405.0 : (selector < 0.667 ? 23404.0 : 18733.0);
                float key = mod(floor(rowCode / exp2(3.0 * v.x)), 8.0);
                float identifier = key + hash(board) * 19.0;
                if (k == 0) {
                    central = identifier;
                    variant = hash(board + key * 2.7 + 8.0);
                } else if (abs(identifier - central) > 0.002) {
                    float d = k == 1 ? min(uv.x, 1.0 - uv.x) : min(uv.y, 1.0 - uv.y);
                    dist = min(dist, d);
                }
            }
            vec4 glaze = variant < 0.34 ? color2 : (variant < 0.68 ? color3 : color4);
            glaze = mix(glaze, color4, min(0.45, glazeMottle * noise(p * 18.0) * 0.45));
            glaze = mix(glaze, color1, bevelDepth * coverage(dist, jointWidth + 0.042) * 0.58);
            result = mix(glaze, color1, coverage(dist, jointWidth));
        """.trimIndent()

        PatternTextureType.VacuumChuck -> """
            vec2 q = fract(p + vec2(phaseX, phaseY)) - 0.5;
            vec2 v = abs(q) - vec2(0.43 - cornerFillet, 0.40 - cornerFillet);
            float hull = length(max(v, vec2(0.0))) + min(max(v.x, v.y), 0.0) - cornerFillet;
            float pitch = 0.397 / grooveCount;
            float channel = abs(fract((-hull) / pitch) - 0.5) * pitch;
            float mask = coverage(channel, 0.15 * pitch) * step(hull, 0.0);
            float milling = noise(vec2(p.x * 4.0, p.y * 126.0));
            result = mix(color2, color3, 0.33 + 0.39 * milling);
            result = mix(result, color4, coverage(channel, 0.27 * pitch) * step(hull, 0.0) * 0.60);
            result = mix(result, color1, mask);
            vec2 port = abs(q) - vec2(0.308, 0.265);
            result = mix(result, color2, coverage(length(port), portRadius + 0.011));
            result = mix(result, color1, coverage(length(port), portRadius));
        """.trimIndent()

        PatternTextureType.TorsionSprings -> """
            vec2 g = p + vec2(phaseX, phaseY);
            vec2 q = fract(g) - 0.5;
            result = mix(color1, color2, 0.08 + 0.11 * noise(p * 20.0));
            for (int depth = 0; depth < 2; depth++) {
                for (int turn = -1; turn <= 1; turn++) {
                    for (int j = 0; j < 10; j++) {
                        float t0 = 0.5 * float(depth) + 0.05 * float(j), t1 = t0 + 0.05;
                        float a0 = t0 * TAU, a1 = t1 * TAU;
                        vec2 a = vec2(float(turn) + t0 - 0.5 + helixProjection * sin(a0),
                                      coilSpan * 0.5 * cos(a0));
                        vec2 b = vec2(float(turn) + t1 - 0.5 + helixProjection * sin(a1),
                                      coilSpan * 0.5 * cos(a1));
                        float d = segment(q, a, b);
                        float metal = 0.5 + 0.5 * cos((t0 + 0.025) * TAU - 0.7);
                        vec4 wire = mix(color2, color3, 0.12 + 0.77 * metal);
                        wire =
                            mix(wire, color4, exp(-d * d / (wireGauge * wireGauge * 0.27)) * metal * 0.7);
                        result = mix(result, wire, coverage(d, wireGauge));
                    }
                }
            }
        """.trimIndent()

        PatternTextureType.CuneiformImpressions -> """
            vec2 g = p * vec2(0.82, 1.0);
            vec2 cell = floor(g), q = fract(g) - 0.5;
            float clay = 0.49 * noise(p * 4.0) + 0.32 * noise(p * 24.0) + 0.19 * noise(p * 78.0);
            result = mix(color2, color3, 0.24 + clay * 0.62);
            for (int i = 0; i < 5; i++) {
                float z = float(i);
                vec2 anchor = vec2(mod(z, 3.0) * 0.245 - 0.225, floor(z / 3.0) * 0.35 - 0.16);
                vec2 d = q - anchor - (hash2(cell + z * 13.0) - 0.5) * 0.12;
                float angle = floor(hash(cell + z * 16.0 + 9.0) * 4.0) * 0.785398;
                vec2 u = rotatePoint(d, angle);
                float len = markSize * (0.55 + 0.29 * hash(cell + z * 21.0));
                vec2 a = vec2(-len * 0.27, -len * 0.31), b = vec2(-len * 0.27, len * 0.31),
                     c = vec2(len * 0.73, 0.0);
                float sd = triangleDistance(u, a, b, c);
                float occupied = step(hash(cell + z * 26.0 + 4.0), markDensity);
                vec4 cut = mix(color1, color2, 0.12 + 0.59 * smoothstep(-0.025, 0.024, u.y + u.x * 0.09));
                result = mix(result, color4, coverage(sd, 0.011) * occupied * stylusDepth * 0.75);
                result = mix(result, cut, coverage(sd, 0.0) * occupied * stylusDepth);
            }
        """.trimIndent()

        PatternTextureType.AnechoicWedges -> """
            vec2 g = p + 2.0 * vec2(phaseX, phaseY);
            vec2 id = floor(g), q = fract(g) - 0.5;
            float checker = mod(id.x + id.y, 2.0);
            float axis = mix(q.x, q.y, checker);
            float t = fract((axis + 0.5) * wedgePitch);
            float h = 1.0 - abs(2.0 * t - 1.0);
            float lighting = mix(0.24, 0.8, step(0.5, t)) * mix(1.0, 0.65, checker);
            float pores = 0.63 * noise(p * 73.0) + 0.37 * noise(p * 173.0);
            vec4 foam = mix(color2, color3, 0.17 + lighting * wedgeDepth * 0.77);
            foam = mix(foam, color1,
                       (1.0 - h) * wedgeDepth * 0.74 + foamPores * 0.35 * smoothstep(0.55, 0.80, pores));
            foam = mix(foam, color4, pow(max(h, 0.0), 12.0) * 0.28);
            float border = min(0.5 - abs(q.x), 0.5 - abs(q.y));
            result = mix(foam, color1, coverage(border, 0.014) * 0.78);
        """.trimIndent()
    }
    val uniforms = type.parameters.joinToString("\n") { "uniform float ${it.name};" }
    val seed = if (type.parameters.any { it.name == "seed" }) "" else "const float seed = 0.0;"
    return """
        precision highp float;
        varying vec2 textureCoordinate;
        uniform vec2 aspect;
        uniform float frequency, offsetX, offsetY, rotation, pixelWidth, phaseX, phaseY;
        uniform vec4 color1, color2, color3, color4;
        $uniforms
        $seed
        const float TAU = 6.28318530718;
        float hash(vec2 p) { return fract(sin(dot(p + seed, vec2(127.1,311.7))) * 43758.5453); }
        vec2 hash2(vec2 p) { return vec2(hash(p), hash(p+19.3)); }
        float safeAngle(vec2 p) { return dot(p,p)<0.0000001?0.0:atan(p.y,p.x); }
        float noise(vec2 p) {
            vec2 i=floor(p),f=fract(p);f=f*f*(3.0-2.0*f);
            return mix(mix(hash(i),hash(i+vec2(1,0)),f.x),mix(hash(i+vec2(0,1)),hash(i+1.0),f.x),f.y);
        }
        float segment(vec2 p,vec2 a,vec2 b) {
            vec2 ab=b-a;return length(p-a-ab*clamp(dot(p-a,ab)/max(dot(ab,ab),0.00001),0.0,1.0));
        }
        vec2 rotatePoint(vec2 p, float angle) {
            float c = cos(angle), s = sin(angle);
            return vec2(c * p.x - s * p.y, s * p.x + c * p.y);
        }
        float ellipseDistance(vec2 p, vec2 radii) {
            return (length(p / radii) - 1.0) * min(radii.x, radii.y);
        }
        float triangleDistance(vec2 p, vec2 a, vec2 b, vec2 c) {
            vec2 ab = b - a, bc = c - b, ca = a - c;
            vec2 side = vec2(ab.x * (p.y - a.y) - ab.y * (p.x - a.x),
                             bc.x * (p.y - b.y) - bc.y * (p.x - b.x));
            float last = ca.x * (p.y - c.y) - ca.y * (p.x - c.x);
            float d = min(segment(p, a, b), min(segment(p, b, c), segment(p, c, a)));
            bool inside = min(min(side.x, side.y), last) >= 0.0 ||
                          max(max(side.x, side.y), last) <= 0.0;
            return inside ? -d : d;
        }
        float coverage(float distance,float width) {
            float aa=max(pixelWidth*.7,.0001);
            return 1.0-smoothstep(width-aa,width+aa,distance);
        }
        vec4 palette(float t) {
            t=fract(t)*4.0;
            if(t<1.0)return mix(color1,color2,t);
            if(t<2.0)return mix(color2,color3,t-1.0);
            if(t<3.0)return mix(color3,color4,t-2.0);
            return mix(color4,color1,t-3.0);
        }
        vec3 hsv(vec3 c) { return c.z*mix(vec3(1),clamp(abs(fract(c.xxx+vec3(0,0.666667,0.333333))*6.0-3.0)-1.0,0.0,1.0),c.y); }
        float layerNoise(vec2 p) {
            return noise(p) * 0.53 + noise(p * 2.07 + 9.1) * 0.27 +
                noise(p * 4.19 + 17.3) * 0.13 + noise(p * 8.37 - 6.4) * 0.07;
        }
        float repeatDistance(float x) {
            return abs(fract(x + 0.5) - 0.5);
        }
        float reliefBand(float d, float w) {
            float aa = max(pixelWidth * 0.65, 0.0003);
            return 1.0 - smoothstep(w - aa, w + aa, abs(d));
        }
        vec3 cellSample(vec2 p) {
            vec2 tile = floor(p), f = fract(p);
            float a = 8.0, b = 8.0, id = 0.0;
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    vec2 o = vec2(float(x), float(y));
                    vec2 delta = o + 0.10 + 0.80 * hash2(tile + o) - f;
                    float ds = dot(delta, delta);
                    b = min(b, max(a, ds));
                    if (ds < a) {
                        a = ds;
                        id = hash(tile + o + 28.1);
                    }
                }
            }
            return vec3(sqrt(a), sqrt(b) - sqrt(a), id);
        }
        vec4 toneRamp(float t) {
            float q = clamp(t, 0.0, 1.0) * 3.0;
            return mix(mix(color1, color2, min(q, 1.0)),
                mix(color3, color4, clamp(q - 2.0, 0.0, 1.0)), clamp(q - 1.0, 0.0, 1.0));
        }

        vec4 surfaceCell(vec2 point, float jitter) {
            vec2 tile = floor(point);
            float nearest = 10.0, second = 10.0;
            vec2 delta = vec2(0.0);
            for (int row = -1; row <= 1; row++) {
                for (int col = -1; col <= 1; col++) {
                    vec2 neighbor = tile + vec2(float(col), float(row));
                    vec2 offset = point - neighbor - mix(vec2(0.5), hash2(neighbor), jitter);
                    float distanceSq = dot(offset, offset);
                    if (distanceSq < nearest) {
                        second = nearest;
                        nearest = distanceSq;
                        delta = offset;
                    } else {
                        second = min(second, distanceSq);
                    }
                }
            }
            return vec4(sqrt(nearest), sqrt(second) - sqrt(nearest), delta);
        }
        void main() {
            vec2 p=(textureCoordinate-.5)*aspect-vec2(offsetX,offsetY);
            float a=radians(rotation);p=mat2(cos(a),-sin(a),sin(a),cos(a))*p*frequency;
            vec4 result=color1;
            { $body }
            gl_FragColor=vec4(result.rgb*result.a,result.a);
        }
    """.trimIndent()
}