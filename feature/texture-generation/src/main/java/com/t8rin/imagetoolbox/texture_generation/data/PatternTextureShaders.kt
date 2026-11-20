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
            vec2 q = fract(p + vec2(0.5 * floor(p.y), 0.0)) - 0.5;
            float stem = 0.06 * sin(4.0 * q.y);
            result = mix(color1, color2, coverage(abs(q.x - stem), 0.013));
            for (int i = 0; i < 4; i++) {
                float side = mod(float(i), 2.0) * 2.0 - 1.0;
                float level = float(i) * 0.23 - 0.34;
                vec2 base = vec2(0.06 * sin(4.0 * level), level);
                vec2 tip = base + vec2(side * 0.20, -0.11);
                result = mix(result, color3, coverage(segment(q, base, tip), 0.009));
                vec2 pod = rotatePoint(q - tip, side * 0.48);
                float edge = ellipseDistance(pod, vec2(0.091, 0.138));
                result = mix(result, mix(color2, color3, 0.64), coverage(edge, 0.0));
                result = mix(result, color4, coverage(abs(edge), 0.006));
                float vein = abs(pod.x - 0.018 * sin(pod.y * 16.0));
                result = mix(result, color4, coverage(vein, 0.003) * coverage(edge, -0.012));
                result = mix(result, color2, coverage(length(pod - vec2(0.021, -0.023)), 0.015));
            }
        """.trimIndent()

        PatternTextureType.HorsetailStems -> """
            vec2 q = p;
            float column = floor(q.x);
            q.x = fract(q.x) - 0.5;
            q.y += hash(vec2(column, 3.7));
            float joint = fract(q.y) - 0.5;
            q.x -= 0.065 * sin(p.y * 2.1 + column * 3.0);
            result = mix(color1, color2, 0.25 + 0.18 * cos(p.x * 4.0));
            for (int i = 1; i <= 5; i++) {
                float t = float(i) / 5.0;
                vec2 branch = vec2(0.09 + 0.32 * t, -0.54 + 0.29 * t);
                float distance = segment(vec2(abs(q.x), joint), vec2(0.0, 0.25), branch);
                result = mix(result, mix(color2, color3, t), coverage(distance, 0.011));
            }
            result = mix(result, color3, coverage(abs(q.x), 0.032));
            result = mix(result, color4, coverage(abs(q.x + 0.013), 0.006));
            result = mix(result, color2, coverage(max(abs(q.x) - 0.043, abs(joint - 0.25) - 0.035), 0.0));
        """.trimIndent()

        PatternTextureType.DandelionClocks -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 q = fract(tile) - 0.5;
            vec2 center = vec2((hash(floor(tile)) - 0.5) * 0.15, -0.11);
            vec2 head = q - center;
            result = mix(color1, color2, 0.12 + 0.12 * noise(p * 3.0));
            float stalk = abs(head.x - 0.10 * head.y * head.y);
            result = mix(result, color2, coverage(stalk, 0.013) * step(0.0, head.y));
            for (int i = 0; i < 28; i++) {
                float n = float(i);
                float angle = n * 2.39996;
                vec2 axis = vec2(cos(angle), sin(angle));
                float span = 0.31 * sqrt((n + 3.0) / 30.0);
                vec2 end = axis * span;
                result = mix(result, color3, coverage(segment(head, vec2(0.0), end), 0.0028));
                vec2 v = vec2(dot(head - end, vec2(-axis.y, axis.x)), dot(head - end, axis));
                float tuft = min(segment(vec2(abs(v.x), v.y), vec2(0.0), vec2(0.027, 0.021)),
                                 segment(v, vec2(0.0), vec2(0.0, 0.03)));
                result = mix(result, color4, coverage(tuft, 0.0045));
            }
            result = mix(result, color3, coverage(length(head), 0.026));
        """.trimIndent()

        PatternTextureType.CalicoKoi -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 q = rotatePoint(fract(tile) - 0.5, (hash(floor(tile)) - 0.5) * 3.0);
            q.y -= 0.08 * sin(q.x * 8.0);
            result = mix(color1, color2, 0.18 * noise(p * 3.0));
            float tail = triangleDistance(q, vec2(0.17, 0.0), vec2(0.40, -0.16), vec2(0.36, 0.13));
            float fins =
                    triangleDistance(vec2(q.x, abs(q.y)), vec2(-0.09, 0.07), vec2(0.05, 0.22), vec2(0.08, 0.06));
            result = mix(result, color2, coverage(min(tail, fins), 0.0));
            float fish = ellipseDistance(q, vec2(0.28, 0.115)) + q.x * 0.12;
            float spots = noise(q * vec2(11.0, 16.0) + floor(tile) * 3.7);
            vec4 scales = mix(color3, color4, smoothstep(0.46, 0.51, spots));
            scales = mix(scales, color1, smoothstep(0.68, 0.74, spots));
            result = mix(result, scales, coverage(fish, 0.0));
            result = mix(result, color1, coverage(length(vec2(q.x + 0.21, abs(q.y) - 0.067)), 0.017));
        """.trimIndent()

        PatternTextureType.ValleyBells -> """
            vec2 q = fract(p + vec2(0.5 * floor(p.y), 0.0)) - 0.5;
            float stem = -0.16 + 0.38 * (q.y + 0.42) * (q.y + 0.42);
            result = mix(color1, color3, coverage(abs(q.x - stem), 0.012));
            vec2 leaf = rotatePoint(q - vec2(0.16, 0.19), -0.40);
            float blade = abs(leaf.x) - 0.11 * max(0.0, 1.0 - pow(abs(leaf.y) / 0.32, 1.3));
            result = mix(result, color2, coverage(max(blade, abs(leaf.y) - 0.32), 0.0));
            result = mix(result, color3, coverage(abs(leaf.x), 0.004) * step(abs(leaf.y), 0.28));
            for (int i = 0; i < 4; i++) {
                float y = -0.34 + float(i) * 0.18;
                float x = -0.16 + 0.38 * (y + 0.42) * (y + 0.42);
                vec2 bell = q - vec2(x - 0.105, y + 0.042);
                result = mix(result, color3, coverage(segment(q, vec2(x, y), vec2(x - 0.10, y + 0.012)), 0.008));
                float rim = 0.057 - 0.014 * cos(bell.x * 98.0);
                float cap = ellipseDistance(bell, vec2(0.054 + 0.28 * clamp(bell.y, 0.0, 0.08), 0.078));
                result = mix(result, color4, coverage(max(cap, bell.y - rim), 0.0));
            }
        """.trimIndent()

        PatternTextureType.SilkPoppies -> """
            vec2 t = vec2(p.x + floor(p.y) * 0.5, p.y);
            vec2 q = fract(t) - 0.5;
            float phase = hash(floor(t)) * TAU;
            float bend = 0.12 * sin(q.y * 3.2 + phase);
            result = mix(color1, color2, coverage(abs(q.x - bend), 0.011) * step(-0.11, q.y));
            q -= vec2(0.12 * sin(-0.11 * 3.2 + phase), -0.11);
            float a = safeAngle(q) + phase;
            float r = length(q);
            float rim = 0.25 + 0.043 * cos(4.0 * a) + 0.018 * sin(7.0 * a);
            vec4 petal =
                    mix(color3, color4, 0.18 + 0.38 * (0.5 + 0.5 * cos(4.0 * a + 0.6)) * smoothstep(0.08, rim, r));
            petal = mix(petal, color4, coverage(abs(r - rim + 0.013), 0.003));
            result = mix(result, petal, coverage(r - rim, 0.0));
            result = mix(result, color1, coverage(r, 0.064 + 0.010 * cos(12.0 * a)));
            result = mix(result, color2, coverage(r, 0.027));
        """.trimIndent()

        PatternTextureType.FlyAgaricGrove -> """
            vec2 t = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 q = fract(t) - 0.5;
            q = rotatePoint(q, (hash(floor(t)) - 0.5) * 0.45);
            float g = 0.07 * noise(p * 22.0);
            result = mix(color1, color2, g + 0.12 * smoothstep(0.22, 0.50, q.y));
            float stalk = ellipseDistance(q - vec2(0.0, 0.14), vec2(0.053, 0.20));
            result = mix(result, color4, coverage(stalk, 0.0));
            float skirt = triangleDistance(q, vec2(0.0, 0.02), vec2(-0.09, 0.13), vec2(0.075, 0.125));
            result = mix(result, mix(color2, color4, 0.65), coverage(skirt, 0.0));
            vec2 cap = q - vec2(0.0, -0.032);
            float brim = 0.038 * cos(cap.x * 6.0);
            float edge = max(ellipseDistance(cap, vec2(0.34, 0.235)), cap.y - brim);
            vec4 paint = mix(color3, color4, 0.17 * smoothstep(0.28, 0.68, noise(q * 10.0)));
            vec2 grid = cap * 14.0;
            vec2 fleck = fract(grid) - 0.5 - (hash2(floor(grid)) - 0.5) * 0.37;
            paint = mix(paint, color4, coverage(length(fleck) / 14.0, 0.010 + 0.009 * hash(floor(grid))));
            result = mix(result, paint, coverage(edge, 0.0));
            result = mix(result, color4, coverage(abs(cap.y - brim), 0.009) * coverage(abs(cap.x), 0.33));
        """.trimIndent()

        PatternTextureType.BeachPebbles -> """
            result = mix(color1, color2, 0.12 * noise(p * 26.0));
            vec2 cell = floor(p);
            for (int row = -1; row <= 1; row++) {
                for (int col = -1; col <= 1; col++) {
                    vec2 id = cell + vec2(float(col), float(row));
                    vec2 center = id + vec2(0.34 + 0.34 * mod(id.y, 2.0), 0.5);
                    vec2 q = rotatePoint(p - center - (hash2(id) - 0.5) * 0.16, (hash(id + 2.0) - 0.5) * 1.3);
                    float edge =
                            ellipseDistance(q, vec2(0.48, 0.30 + hash(id + 5.0) * 0.11)) + 0.015 * sin(q.x * 13.0);
                    vec4 stone = mix(color2, color3, hash(id + 7.0));
                    float band = abs(q.y - 0.18 * q.x - 0.075 * sin(3.8 * q.x + hash(id) * TAU));
                    stone = mix(stone, color4, coverage(band, 0.014 + 0.028 * hash(id + 1.0)));
                    stone = mix(stone, color1, 0.22 * smoothstep(0.0, 0.40, q.y + q.x * 0.35));
                    result = mix(result, stone, coverage(edge, 0.0));
                }
            }
        """.trimIndent()

        PatternTextureType.TravertinePools -> """
            vec2 q = vec2(p.x, p.y * 1.75);
            float ridge = q.y + 0.36 * sin(3.0 * q.x + 2.4 * q.y) + 0.54 * noise(q * 2.1) + 0.09 * noise(q * 9.0);
            float level = fract(ridge * 2.15);
            vec4 pool = mix(color1, color2, smoothstep(0.04, 0.60, level));
            pool = mix(pool, color3, smoothstep(0.60, 0.96, level));
            float lip = 1.0 - smoothstep(0.022, 0.095, abs(level - 0.91));
            result = mix(pool, color4, lip);
            result = mix(result, color2, 0.12 * noise(q * 30.0));
        """.trimIndent()

        PatternTextureType.TidepoolStars -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 id = floor(tile);
            vec2 q = rotatePoint(fract(tile) - 0.5 - (hash2(id) - 0.5) * 0.10, hash(id) * TAU);
            result = mix(color1, color2, 0.18 + 0.42 * noise(p * 4.5));
            float angle = safeAngle(q);
            float radius = length(q);
            float sector = mod(angle + TAU / 10.0, TAU / 5.0) - TAU / 10.0;
            float arm = 0.11 + 0.275 * pow(max(0.0, cos(sector * 2.5)), 6.0);
            arm -= 0.055 * abs(sin(sector * 5.0));
            float edge = radius - arm;
            float centerline = radius * abs(sin(sector + 0.13 * sin(radius * 10.0)));
            vec4 skin = mix(color3, color4, 0.24 * (1.0 - smoothstep(0.0, 0.075, centerline)));
            float beads = length(vec2(centerline - 0.017, (fract(radius * 31.0) - 0.5) / 31.0));
            skin = mix(skin, color4, coverage(beads, 0.009));
            result = mix(result, skin, coverage(edge, 0.0));
        """.trimIndent()

        PatternTextureType.WaveCutCliffs -> """
            vec2 q = p + vec2(0.5);
            float sky = 0.11 * noise(q * 7.0);
            result = mix(color4, color3, 0.18 + sky);
            float crest = 0.32 + 0.14 * sin(q.x * 4.4) + 0.07 * noise(q * vec2(9.0, 0.0));
            float foot = 0.67 + 0.12 * sin(q.x * 3.4 + 0.2) + 0.045 * noise(q * vec2(19.0, 0.0));
            float bed = q.y * 22.0 + q.x * 2.7 + 0.34 * sin(q.x * 8.0) + 0.20 * noise(q * 14.0);
            vec4 rock = mix(color3, color4, 0.52 * smoothstep(0.18, 0.74, fract(bed)));
            rock = mix(rock, color1, 0.35 * smoothstep(0.88, 0.99, fract(bed * 0.67)));
            result = mix(result, rock, smoothstep(crest - pixelWidth, crest + pixelWidth, q.y));
            vec4 sea = mix(color1, color2, 0.35 + 0.60 * noise(q * vec2(6.0, 42.0)));
            float surf = abs(q.y - foot - 0.017 - 0.009 * sin(42.0 * q.x));
            sea = mix(sea, color4, coverage(surf, 0.009));
            result = mix(result, sea, smoothstep(foot - pixelWidth, foot + pixelWidth, q.y));
        """.trimIndent()

        PatternTextureType.SailingRegatta -> """
            vec2 tile = vec2(p.x + floor(p.y) * 0.5, p.y);
            vec2 q = fract(tile) - 0.5;
            if (hash(floor(tile)) > 0.5) q.x = -q.x;
            float wake = abs(q.y - 0.30 - 0.025 * sin(q.x * 13.0));
            result = mix(color1, color2, max(coverage(wake, 0.012), 0.40 * coverage(abs(wake - 0.09), 0.006)));
            float sail = triangleDistance(q, vec2(-0.035, -0.37), vec2(-0.055, 0.11), vec2(-0.34, 0.11));
            result = mix(result, color4, coverage(sail, 0.0));
            float jib = triangleDistance(q, vec2(0.015, -0.25), vec2(0.015, 0.10), vec2(0.26, 0.10));
            result = mix(result, color3, coverage(jib, 0.0));
            float hull = max(abs(q.y - 0.18) - 0.042, abs(q.x + 0.03) + 0.8 * (q.y - 0.136) - 0.35);
            result = mix(result, color2, coverage(hull, 0.0));
            result = mix(result, color4, coverage(segment(q, vec2(-0.028, -0.36), vec2(-0.028, 0.14)), 0.006));
        """.trimIndent()

        PatternTextureType.BarnacleColony -> """
            vec2 tile = vec2(p.x + floor(p.y) * 0.5, p.y);
            vec2 id = floor(tile);
            vec2 q = fract(tile) - 0.5 - (hash2(id) - 0.5) * 0.09;
            float r = length(q);
            float a = safeAngle(q);
            float size = 0.27 + 0.14 * hash(id + 4.8);
            float rim = size * (1.0 + 0.065 * sin(6.0 * a + hash(id) * TAU) + 0.035 * cos(9.0 * a));
            result = mix(color1, color2, 0.28 * noise(p * 8.0));
            float ridges = smoothstep(0.25, 0.88, 0.5 + 0.5 * cos(22.0 * a + r * 5.0));
            vec4 shell = mix(color4, color3, 0.48 * ridges + 0.30 * (1.0 - r / rim));
            result = mix(result, shell, coverage(r - rim, 0.0));
            vec2 mouth = rotatePoint(q - vec2(0.025, -0.04), hash(id + 7.0) * 3.0);
            float throat = abs(mouth.x) * 1.65 + abs(mouth.y) - rim * 0.47;
            result = mix(result, color1, coverage(throat, 0.0));
            result = mix(result, color2,
                         coverage(abs(mouth.x + 0.03 * sin(mouth.y * 18.0)), 0.009) * coverage(throat, -0.015));
        """.trimIndent()

        PatternTextureType.FlyingPetrels -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 q = rotatePoint(fract(tile) - 0.5 - (hash2(floor(tile)) - 0.5) * 0.08,
                                 (hash(floor(tile)) - 0.5) * 1.1);
            float wind = sin(p.y * 11.0 + 0.8 * sin(p.x * 2.7) + 2.0 * noise(p * 0.8));
            result = mix(color2, color3, 0.38 + 0.30 * wind);
            q.x = abs(q.x);
            float outer = triangleDistance(q, vec2(0.0, 0.065), vec2(0.19, -0.14), vec2(0.43, -0.115));
            float inner = triangleDistance(q, vec2(0.0, 0.065), vec2(0.19, -0.14), vec2(0.085, 0.085));
            result = mix(result, color1, coverage(min(outer, inner), 0.0));
            float flash = triangleDistance(q, vec2(0.05, 0.017), vec2(0.19, -0.118), vec2(0.29, -0.119));
            result = mix(result, color4, coverage(flash, 0.0));
            result = mix(result, color1, coverage(ellipseDistance(q - vec2(0.0, 0.074), vec2(0.023, 0.077)), 0.0));
        """.trimIndent()

        PatternTextureType.MoonPhases -> """
            vec2 grid = p * vec2(1.6, 1.0);
            vec2 id = floor(grid);
            vec2 q = (fract(grid) - 0.5) / vec2(1.6, 1.0);
            float phase = mod(id.x + id.y * 3.0, 8.0) * TAU / 8.0;
            float radius = 0.23;
            float r = length(q);
            float z = sqrt(max(0.0, radius* radius - dot(q, q)));
            float illumination = (q.x * sin(phase) + z * cos(phase)) / radius;
            vec4 moon = mix(color2, color4, smoothstep(-0.014, 0.014, illumination));
            moon = mix(moon, color3, 0.26 * smoothstep(0.44, 0.73, noise(q * 34.0)));
            result = mix(color1, moon, coverage(r, radius));
            result = mix(result, color3, 0.55 * coverage(abs(r - radius), 0.0035));
            float tick = segment(q, vec2(0.0, 0.31), vec2(0.0, 0.344));
            result = mix(result, color3, coverage(tick, 0.006));
        """.trimIndent()

        PatternTextureType.SiderealTrails -> """
            vec2 q = p - vec2(-0.28, 0.22);
            float r = length(q);
            float ring = floor(r * 115.0);
            float angle = fract(safeAngle(q) / TAU + 0.5 + hash(vec2(ring, 1.0)));
            float span = 0.08 + 0.40 * hash(vec2(ring, 4.0));
            float edge = abs(fract(r * 115.0) - 0.5) / 115.0;
            float arc = coverage(edge, 0.00075 + 0.0011 * hash(vec2(ring, 3.0))) *
                        (1.0 - smoothstep(span - 0.008, span, angle));
            result = mix(color1, color2, 0.29 * noise(p * 2.4) + 0.12 * noise(p * 7.0));
            vec4 light = mix(color3, color4, hash(vec2(ring, 7.0)));
            result = mix(result, light, arc * (0.22 + 0.78 * pow(clamp(angle / span, 0.0, 1.0), 0.8)));
        """.trimIndent()

        PatternTextureType.OrionChart -> """
            vec2 q = p;
            float grid = min(abs(fract(q.x * 8.0 + 0.5) - 0.5), abs(fract(q.y * 8.0 + 0.5) - 0.5)) / 8.0;
            result = mix(color1, color2, 0.26 * coverage(grid, 0.001));
            vec2 sky = p * 40.0;
            vec2 star = fract(sky) - 0.15 - 0.70 * hash2(floor(sky));
            float distant = coverage(length(star) / 40.0, 0.0014) * step(0.76, hash(floor(sky) + 7.0));
            result = mix(result, color4, 0.65 * distant);
            vec2 saiph = vec2(-0.13, 0.35), rigel = vec2(0.18, 0.29);
            vec2 bellatrix = vec2(0.14, -0.23), betelgeuse = vec2(-0.20, -0.27);
            vec2 head = vec2(-0.005, -0.37);
            vec2 mintaka = vec2(0.059, 0.010), alnilam = vec2(-0.007, 0.050), alnitak = vec2(-0.075, 0.094);
            float line = min(segment(q, saiph, alnitak), segment(q, rigel, mintaka));
            line = min(line, min(segment(q, mintaka, bellatrix), segment(q, alnitak, betelgeuse)));
            line = min(line, min(segment(q, betelgeuse, head), segment(q, head, bellatrix)));
            line = min(line, min(segment(q, mintaka, alnilam), segment(q, alnilam, alnitak)));
            result = mix(result, color3, coverage(line, 0.0022));
            float stars = min(min(length(q - rigel) - 0.017, length(q - betelgeuse) - 0.020),
                              min(length(q - bellatrix) - 0.014, length(q - saiph) - 0.012));
            stars = min(stars,
                        min(length(q - head) - 0.009,
                            min(length(q - mintaka), min(length(q - alnilam), length(q - alnitak))) - 0.011));
            result = mix(result, color2, coverage(abs(stars - 0.009), 0.002));
            result = mix(result, color4, coverage(stars, 0.0));
        """.trimIndent()

        PatternTextureType.MeteorShower -> """
            vec2 q = rotatePoint(p, 0.58);
            result = mix(color1, color2, 0.33 * noise(q * 2.0));
            vec2 stars = q * 28.0;
            float dust = length(fract(stars) - 0.12 - 0.76 * hash2(floor(stars)));
            result = mix(result, color4, coverage(dust / 28.0, 0.0018) * step(0.78, hash(floor(stars) + 0.8)));
            vec2 track = vec2(q.x * 9.0, q.y * 1.45);
            vec2 id = floor(track);
            float center = 0.18 + 0.64 * hash(id + 5.1);
            float head = 0.13 + 0.54 * hash(id);
            vec2 delta = (fract(track) - vec2(center, head)) / vec2(9.0, 1.45);
            float span = 0.13 + 0.12 * hash(id + 7.0);
            float fade = smoothstep(-span, 0.0, delta.y) * step(delta.y, 0.0);
            result = mix(result, color3, coverage(abs(delta.x), 0.004) * fade);
            result = mix(result, color4, coverage(length(delta), 0.006 + 0.004 * hash(id + 3.0)));
        """.trimIndent()

        PatternTextureType.ClockworkOrrery -> """
            vec2 q = p;
            float r = length(q);
            result = mix(color1, color2, 0.16 * (1.0 - smoothstep(0.0, 0.65, r)));
            for (int i = 0; i < 6; i++) {
                float n = float(i);
                float orbit = 0.10 + n * 0.065;
                float angle = 0.65 + n * 2.36 + time / (1.0 + 0.48 * n);
                vec2 planet = orbit * vec2(cos(angle), sin(angle));
                result = mix(result, color2, coverage(abs(r - orbit), 0.002));
                result = mix(result, color3, 0.68 * coverage(segment(q, planet * 0.17, planet), 0.0026));
            }
            for (int i = 0; i < 6; i++) {
                float n = float(i);
                float angle = 0.65 + n * 2.36 + time / (1.0 + 0.48 * n);
                vec2 planet = (0.10 + n * 0.065) * vec2(cos(angle), sin(angle));
                vec2 v = q - planet;
                float radius = 0.016 + 0.011 * mod(n, 3.0);
                vec4 metal = mix(color3, color4, 1.0 - smoothstep(0.0, radius * 1.6, length(v + vec2(0.006))));
                result = mix(result, metal, coverage(length(v), radius));
            }
            result = mix(result, color4, coverage(r, 0.047));
            result = mix(result, color3, coverage(abs(r - 0.057), 0.003));
        """.trimIndent()

        PatternTextureType.CometPlume -> """
            vec2 q = rotatePoint(p - vec2(0.24, 0.17), -0.48);
            float trail = max(-q.x, 0.0);
            result = mix(color1, color2, 0.22 * noise(p * 4.0));
            vec2 sky = p * 45.0;
            float stars = length(fract(sky) - 0.12 - 0.76 * hash2(floor(sky))) / 45.0;
            result = mix(result, color4, 0.8 * coverage(stars, 0.0012) * step(0.87, hash(floor(sky))));
            for (int i = 0; i < 9; i++) {
                float n = float(i) / 8.0;
                float bend = trail * trail * (n - 0.12) * 0.66 + trail * (n - 0.5) * 0.32;
                float width = 0.004 + trail * (0.012 + 0.012 * n);
                float plume = exp(-abs(q.y - bend) / width) * exp(-1.8 * trail) * step(q.x, 0.0);
                result = mix(result, mix(color2, color3, n), plume * 0.78);
            }
            float head = length(q);
            result = mix(result, color3, 0.8 * exp(-head * 38.0));
            result = mix(result, color4, coverage(head, 0.016));
        """.trimIndent()

        PatternTextureType.BinarySunset -> """
            vec2 q = p;
            result = mix(color2, color3, smoothstep(-0.5, 0.33, q.y));
            result = mix(result, color4, coverage(length(q - vec2(0.16, -0.16)), 0.108));
            result = mix(result, color4, coverage(length(q - vec2(-0.032, -0.097)), 0.053));
            for (int i = 0; i < 4; i++) {
                float layer = float(i);
                float crest = 0.08 + 0.093 * layer + 0.048 * sin(5.7 * q.x + layer * 2.6) +
                              0.058 * noise(vec2(q.x * (5.0 - layer * 0.7), layer * 19.0));
                vec4 hill = mix(color2, color1, 0.15 + layer * 0.28);
                result = mix(result, hill, smoothstep(crest - pixelWidth, crest + pixelWidth, q.y));
            }
        """.trimIndent()

        PatternTextureType.PivotFields -> """
            vec2 tile = floor(p);
            vec2 q = fract(p) - 0.5;
            float field = hash(tile + 1.0);
            float furrow = cos((q.x * 0.4 + q.y) * 130.0);
            result = mix(color1, color3, 0.30 + 0.25 * field + 0.08 * furrow);
            vec2 c = q - (hash2(tile) * 0.06 - 0.03);
            float r = length(c);
            float a = fract(safeAngle(c) / TAU + field);
            vec4 crop = mix(color2, color4, hash(tile + 4.0) * 0.9);
            crop = mix(crop, color1, 0.12 + 0.10 * cos(r * 168.0));
            float gap = 0.08 + 0.2 * hash(tile + 2.0);
            float tractor = smoothstep(gap, gap + pixelWidth * 0.60, a);
            result = mix(result, crop, coverage(r, 0.42) * tractor);
            float road = min(0.5 - abs(q.x), 0.5 - abs(q.y));
            result = mix(result, color3, coverage(road, 0.018));
        """.trimIndent()

        PatternTextureType.AlluvialBends -> """
            vec2 q = rotatePoint(p, 0.43);
            float center = 0.20 * sin(q.y * 9.0) + 0.094 * sin(q.y * 17.0 + 0.7);
            float distanceToWater = abs(q.x - center) / (1.0 + 0.48 * abs(cos(q.y * 9.0)));
            float levee = distanceToWater + 0.025 * noise(q * 13.0);
            float beds = 0.5 + 0.5 * cos(levee * 110.0 + 1.25 * sin(q.y * 9.0));
            result = mix(color3, color4, 0.15 + 0.52 * beds + 0.10 * noise(p * 20.0));
            result = mix(result, color4, coverage(distanceToWater, 0.060));
            vec4 water = mix(color1, color2, 0.20 + 0.58 * smoothstep(0.0, 0.051, distanceToWater));
            result = mix(result, water, coverage(distanceToWater, 0.049));
        """.trimIndent()

        PatternTextureType.RakedSandGarden -> """
            vec2 q = p;
            float field = q.y + 0.06 * sin(q.x * 5.0);
            float pebble = 2.0;
            for (int i = 0; i < 3; i++) {
                vec2 center = i == 0 ? vec2(-0.22, -0.13) : (i == 1 ? vec2(0.08, 0.21) : vec2(0.23, -0.10));
                vec2 v = rotatePoint(q - center, 0.4 + float(i) * 0.7);
                vec2 radii = vec2(0.10, 0.062) * (1.0 - 0.12 * float(i));
                float d = ellipseDistance(v, radii);
                pebble = min(pebble, d);
                float weight = 1.0 - smoothstep(0.06, 0.17, d);
                field = mix(field, d + center.y, weight);
            }
            float rake = 0.5 + 0.5 * cos(field * 170.0);
            result = mix(color2, color4, 0.30 + 0.67 * rake);
            result = mix(result, color3, 0.22 * noise(p * 90.0));
            vec4 stone = mix(color1, color2, 0.7 * noise(p * 18.0) + 0.12 * noise(p * 70.0));
            result = mix(result, stone, coverage(pebble, 0.0));
        """.trimIndent()

        PatternTextureType.BalloonFestival -> """
            vec2 cell = floor(p + vec2(0.5 * floor(p.y), 0.0));
            vec2 q = fract(p + vec2(0.5 * floor(p.y), 0.0)) - 0.5;
            q -= (hash2(cell) - 0.5) * 0.13;
            result = mix(color1, color2, 0.22 * noise(p * 2.0));
            vec2 bulb = q - vec2(0.0, -0.12);
            float height = (bulb.y + 0.025) / 0.24;
            float r = 0.224 * sqrt(max(0.0, 1.0 - height * height));
            r *= 1.0 - 0.56 * smoothstep(-0.03, 0.21, bulb.y);
            float edge = max(abs(bulb.x) - r, abs(bulb.y + 0.025) - 0.24);
            float panel = asin(clamp(bulb.x / max(r, 0.001), -1.0, 1.0));
            float stripes = smoothstep(-0.04, 0.04, cos(panel * 8.0));
            vec4 canvas = mix(color2, color3, stripes);
            canvas = mix(canvas, color4, 0.13 * smoothstep(-0.4, 1.5, panel));
            result = mix(result, canvas, coverage(edge, 0.0));
            float cable = segment(vec2(abs(q.x), q.y), vec2(0.068, 0.023), vec2(0.035, 0.19));
            result = mix(result, color4, coverage(cable, 0.006));
            float basket = max(abs(q.x) - 0.048, abs(q.y - 0.213) - 0.032);
            result = mix(result, color4, coverage(basket, 0.0));
        """.trimIndent()

        PatternTextureType.HarborLight -> """
            vec2 q = p;
            result = mix(color1, color2, 0.3 + 0.32 * smoothstep(-0.5, 0.30, q.y));
            vec2 lamp = vec2(-0.16, -0.20);
            vec2 beam = q - lamp;
            float ray = abs(beam.y + beam.x * 0.12) - beam.x * 0.21;
            result = mix(result, color4, coverage(ray, 0.0) * step(0.0, beam.x) * 0.55);
            float sea = q.y - 0.27;
            result = mix(result, color1, smoothstep(-pixelWidth, pixelWidth, sea));
            float wave = abs(fract((q.y + 0.003 * sin(q.x * 34.0)) * 48.0) - 0.5) / 48.0;
            result = mix(result, color2,
                         0.65 * coverage(wave, 0.0019) * step(0.0, sea) *
                                 smoothstep(0.25, 0.70, noise(q* vec2(19.0, 70.0))));
            vec2 v = q - lamp;
            float tower = max(abs(v.x) - 0.026 - 0.066 * v.y, max(0.024 - v.y, v.y - 0.50));
            vec4 paint = mix(color4, color3, step(0.5, fract(v.y * 7.0)));
            result = mix(result, paint, coverage(tower, 0.0));
            result = mix(result, color4, coverage(max(abs(v.x) - 0.027, abs(v.y) - 0.023), 0.0));
            float roof = triangleDistance(v, vec2(-0.058, -0.020), vec2(0.0, -0.069), vec2(0.058, -0.020));
            result = mix(result, color1, coverage(roof, 0.0));
            float bluff = 0.31 + 0.10 * sin(q.x * 6.0 + 1.0) + 0.07 * q.x + 0.030 * noise(q * vec2(19.0, 0.0));
            result = mix(result, color1, smoothstep(bluff - pixelWidth, bluff + pixelWidth, q.y));
        """.trimIndent()

        PatternTextureType.PaperLanterns -> """
            vec2 tile = vec2(p.x + floor(p.y) * 0.5, p.y);
            vec2 q = fract(tile) - 0.5;
            float drop = 0.054 * sin((floor(tile.x) + floor(tile.y) * 2.0) * 2.3);
            q.y -= drop;
            float string = abs(q.x);
            result = mix(color1, color4, 0.68 * coverage(string, 0.005) * step(q.y, -0.22));
            float d = ellipseDistance(q, vec2(0.285, 0.266));
            vec4 paper = mix(color2, color3, 0.25 + 0.73 * (1.0 - smoothstep(0.0, 0.3, abs(q.x + 0.05))));
            float ribs = abs(fract((q.y - 0.035 * q.x * q.x * 9.0) * 22.0) - 0.5) / 22.0;
            paper = mix(paper, color2, 0.6 * coverage(ribs, 0.003));
            result = mix(result, paper, coverage(d, 0.0));
            float cap = max(abs(q.x) - 0.088, abs(abs(q.y) - 0.247) - 0.019);
            result = mix(result, color4, coverage(cap, 0.0));
            result = mix(result, color3, coverage(segment(q, vec2(0.0, 0.26), vec2(0.0, 0.32)), 0.008));
            float fringe = triangleDistance(q, vec2(0.0, 0.31), vec2(-0.034, 0.41), vec2(0.038, 0.40));
            result = mix(result, color4, coverage(fringe, 0.0));
        """.trimIndent()

        PatternTextureType.ParasolMarket -> """
            result = color1;
            vec2 tile = floor(p);
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    vec2 id = tile + vec2(float(x), float(y));
                    vec2 c = id + vec2(0.32 + 0.34 * mod(id.y, 2.0), 0.5) + (hash2(id) - 0.5) * 0.08;
                    vec2 v = p - c;
                    float a = safeAngle(v) + hash(id + 2.0) * TAU;
                    float sector = mod(a, TAU / 8.0) - TAU / 16.0;
                    float r = length(v);
                    float size = 0.46 + 0.085 * hash(id + 1.0);
                    float edge = r * cos(sector) - size * cos(TAU / 16.0);
                    float wedge = floor(a * 8.0 / TAU);
                    vec4 canopy = mod(wedge, 2.0) < 1.0 ? color3 : mix(color2, color4, hash(id));
                    canopy = mix(canopy, color4, 0.26 * (1.0 - abs(sector) / (TAU / 16.0)));
                    canopy = mix(canopy, color1, 0.46 * coverage(abs(abs(sector) - TAU / 16.0) * r, 0.0035));
                    canopy = mix(canopy, color2, 0.56 * coverage(abs(edge + 0.065), 0.009));
                    canopy = mix(canopy, color4, coverage(r, 0.023));
                    result = mix(result, canopy, coverage(edge, 0.0));
                }
            }
        """.trimIndent()

        PatternTextureType.BackgammonPoints -> """
            vec2 board = vec2(p.x * 3.0, p.y);
            float column = floor(board.x);
            vec2 q = vec2(fract(board.x) - 0.5, abs(fract(board.y) - 0.5));
            float bar = abs(fract(board.x / 6.0) - 0.5) * 6.0;
            float edge = abs(q.y - 0.486);
            result = mix(color1, color4, coverage(bar, 0.13));
            vec4 point = mod(column, 2.0) < 1.0 ? color2 : color3;
            result = mix(result, point, coverage(abs(q.x), max(0.0, q.y - 0.045)));
            result = mix(result, color4, coverage(edge, 0.011));
            float side = sign(fract(board.y) - 0.5);
            float pieces = floor(hash(vec2(column, floor(board.y) + side)) * 3.6);
            for (int i = 0; i < 3; i++) {
                if (float(i) >= pieces) break;
                vec2 disk = vec2(q.x / 3.0, q.y - (0.425 - 0.105 * float(i)));
                float r = length(disk);
                vec4 checker = mod(column + step(0.0, side), 2.0) < 1.0 ? color4 : color2;
                result = mix(result, color4, coverage(r, 0.057));
                result = mix(result, checker, coverage(r, 0.049));
                result = mix(result, mix(checker, point, 0.58), coverage(abs(r - 0.035), 0.004));
            }
        """.trimIndent()

        PatternTextureType.DominoTable -> """
            vec2 tile = vec2(p.x, p.y * 0.63);
            tile.x += 0.5 * mod(floor(tile.y), 2.0);
            vec2 cell = floor(tile), q = (fract(tile) - 0.5) * vec2(1.0, 1.59);
            float angle = 0.22 * (hash(cell) - 0.5);
            q = mat2(cos(angle), -sin(angle), sin(angle), cos(angle)) * q;
            vec2 edge = abs(q) - vec2(0.276, 0.585);
            float boundary = length(max(edge, 0.0)) + min(max(edge.x, edge.y), 0.0);
            result = mix(color1, color3, coverage(boundary, 0.102));
            result = mix(result, color4, coverage(boundary, 0.079));
            result = mix(result, color2, coverage(boundary, 0.055));
            float inside = coverage(boundary, 0.022);
            result = mix(result, color3, inside* coverage(abs(q.y), 0.012));
            float halfId = step(0.0, q.y);
            q.y = abs(q.y) - 0.32;
            float value = 1.0 + floor(6.0 * hash(cell + vec2(halfId * 11.0, 7.0)));
            vec2 pip = floor(q / 0.169 + 0.5);
            float on = 0.0;
            if (max(abs(pip.x), abs(pip.y)) < 1.5) {
                if (dot(pip, pip) < 0.5)
                    on = mod(value, 2.0);
                else if (pip.x * pip.y > 0.5)
                    on = step(2.0, value);
                else if (pip.x * pip.y < -0.5)
                    on = step(4.0, value);
                else if (abs(pip.x) > 0.5)
                    on = step(6.0, value);
            }
            result = mix(result, color3, on* coverage(length(q - pip * 0.169), 0.053));
        """.trimIndent()

        PatternTextureType.PlayingCardSuits -> """
            vec2 cell = floor(p), q = fract(p) - 0.5;
            vec2 corner = abs(q);
            float suit = mod(cell.x + 3.0 * cell.y, 4.0);
            float mark;
            if (suit < 0.5 || suit > 2.5) {
                vec2 h = q * vec2(1.0, suit < 0.5 ? -1.0 : 1.0);
                float lobes = length(vec2(abs(h.x) - 0.107, h.y - 0.078)) - 0.137;
                float point = max(abs(h.x) * 0.82 - h.y * 0.68 - 0.165, h.y - 0.09);
                mark = min(lobes, point);
                if (suit > 2.5)
                    mark = min(mark, max(abs(q.x) - 0.036 - 0.55 * max(q.y - 0.17, 0.0), abs(q.y - 0.19) - 0.067));
            } else if (suit < 1.5) {
                mark = abs(q.x) + 0.75 * abs(q.y) - 0.195;
            } else {
                float crown = min(length(q - vec2(0.0, -0.11)), length(vec2(abs(q.x) - 0.127, q.y - 0.015))) - 0.13;
                mark = min(crown, max(abs(q.x) - 0.037 - 0.5 * max(q.y - 0.12, 0.0), abs(q.y - 0.16) - 0.09));
            }
            float trim = abs(max(corner.x, corner.y) - 0.466);
            result = mix(color1, color4, coverage(trim, 0.009));
            result = mix(result, color4, coverage(abs(corner.x - 0.362) + abs(corner.y - 0.362), 0.034));
            result = mix(result, suit < 1.5 ? color2 : color3, coverage(mark, 0.0));
        """.trimIndent()

        PatternTextureType.VinylCrate -> """
            vec2 tile = p + vec2(0.35 * floor(p.y), 0.0);
            vec2 cell = floor(tile), q = fract(tile) - 0.5;
            float edition = mod(cell.x + cell.y, 3.0);
            vec4 sleeve = edition < 1.0 ? color1 : (edition < 2.0 ? color3 : color4);
            result = sleeve;
            vec2 disc = q - vec2(0.055, -0.045);
            float radius = length(disc), angle = safeAngle(disc);
            float grooves = 0.5 + 0.5 * cos(radius * TAU * 40.0);
            float sheen = pow(abs(cos(angle + edition * 0.42)), 7.0);
            vec4 vinyl = mix(color2, color4, 0.035 + sheen * (0.07 + 0.16 * grooves));
            result = mix(result, vinyl, coverage(radius, 0.411));
            result = mix(result, edition < 1.0 ? color3 : color1, coverage(radius, 0.139));
            result = mix(result, color4, coverage(abs(radius - 0.109), 0.006));
            result = mix(result, color2, coverage(radius, 0.018));
            float hem = coverage(abs(q.y - 0.428), 0.012) + coverage(abs(q.y - 0.462), 0.005);
            result = mix(result, color2, min(hem, 1.0) * 0.84);
        """.trimIndent()

        PatternTextureType.MusicBoxRoll -> """
            float lane = floor(p.y * 2.0);
            vec2 q = vec2(p.x + 0.5 * mod(lane, 2.0), fract(p.y * 2.0));
            float edge = min(q.y, 1.0 - q.y);
            result = mix(color3, color1, smoothstep(0.024, 0.047, edge));
            float guide = abs(fract(q.x * 8.0) - 0.5);
            result = mix(result, color3, 0.22 * coverage(guide * 0.125, 0.003));
            vec2 peg = vec2((fract(q.x * 4.0) - 0.5) * 0.25, (edge - 0.101) * 0.5);
            result = mix(result, color4, coverage(length(peg - vec2(0.0, 0.006)), 0.023));
            result = mix(result, color2, coverage(length(peg), 0.019));
            vec2 score = vec2(q.x * 8.0, (q.y - 0.17) * 11.0);
            vec2 note = floor(score);
            float pitch = 3.5 + 2.0 * sin(note.x * 0.82 + lane * 1.7) + 1.4 * sin(note.x * 0.37 - lane);
            float sounds =
                    max(coverage(abs(note.y - pitch), 0.72), 0.8 * coverage(abs(note.y - pitch - 2.7), 0.47));
            vec2 slot = abs(fract(score) - 0.5) - vec2(0.36, 0.23);
            float hole = max(slot.x * 0.125, slot.y / 22.0);
            float inScore = step(0.17, q.y) * step(q.y, 0.82);
            result = mix(result, color2, coverage(hole, 0.0) * sounds * inScore);
        """.trimIndent()

        PatternTextureType.ZipperTape -> """
            vec2 q = vec2(fract(p.x) - 0.5, p.y);
            float side = sign(q.x), x = abs(q.x);
            float warp = 0.5 + 0.5 * sin((p.x * 31.0 + p.y * 17.0) * TAU);
            result = mix(color1, color2, 0.12 + 0.13 * warp);
            result = mix(result, color2, coverage(x, 0.146));
            vec2 stitch = vec2(x - 0.253, (fract(p.y * 6.0) - 0.5) / 6.0);
            result = mix(result, color4,
                         0.72 * coverage(segment(stitch, vec2(0.0, -0.039), vec2(0.0, 0.039)), 0.007));
            float y = (fract(p.y * 7.0 + 0.5 * step(0.0, side)) - 0.5) / 7.0;
            float tooth = min(segment(vec2(x, y), vec2(0.038, 0.0), vec2(0.121, 0.0)),
                              length(vec2(x - 0.038, y + 0.015)) - 0.012);
            result = mix(result, color3, coverage(tooth, 0.034));
            result = mix(result, color4, coverage(tooth, 0.020));
            vec2 pull = vec2(q.x, fract(p.y * 0.58 + 0.36 * floor(p.x)) / 0.58 - 0.62);
            float slider = length((pull - vec2(0.0, 0.135)) / vec2(0.143, 0.182));
            result = mix(result, color3, coverage(slider * 0.143, 0.143));
            float tab = segment(pull, vec2(0.0, 0.14), vec2(0.0, -0.118));
            result = mix(result, color2, coverage(tab, 0.08));
            result = mix(result, color4, coverage(tab, 0.065));
            result = mix(result, color2, coverage(length(pull - vec2(0.0, -0.107)), 0.034));
        """.trimIndent()

        PatternTextureType.BobbinLace -> """
            float row = floor(p.y);
            vec2 q = vec2(p.x + 0.5 * mod(row, 2.0), fract(p.y) - 0.5);
            float wave = 0.235 + 0.095 * cos(q.x * TAU);
            float scallop = abs(abs(q.y) - wave);
            float petal = 1.0;
            for (int i = -1; i <= 1; i++) {
                vec2 center = vec2(floor(q.x) + float(i) + 0.5, 0.0);
                vec2 v = q - center;
                float a = safeAngle(v), r = length(v);
                petal = min(petal, abs(r - 0.178 - 0.086 * cos(4.0 * a)));
            }
            float meshA = abs(fract((q.x * 0.84 + q.y) * 5.0) - 0.5) / 6.5;
            float meshB = abs(fract((q.x * 0.84 - q.y) * 5.0) - 0.5) / 6.5;
            result = mix(color1, color2, coverage(min(meshA, meshB), 0.008) * coverage(abs(q.y), wave));
            result = mix(result, color3, coverage(min(petal, scallop), 0.026));
            result = mix(result, color4, coverage(min(petal, scallop), 0.012));
            vec2 picot = vec2((fract(q.x * 8.0) - 0.5) / 8.0, abs(q.y) - wave - 0.031);
            result = mix(result, color3, coverage(abs(length(picot) - 0.027), 0.009));
        """.trimIndent()

        PatternTextureType.MarquetryCompass -> """
            vec2 q = fract(p + 0.5) - 0.5;
            float radius = length(q), angle = safeAngle(q);
            float sector = floor((angle / TAU + 1.0) * 8.0 + 0.5);
            float local = (fract(angle / TAU * 8.0 + 0.5) - 0.5) * TAU / 8.0;
            float tip = mod(sector, 2.0) < 0.5 ? 0.448 : 0.316;
            float limit =
                    0.139 * tip /
                    (0.139 * cos(local) + (tip - 0.139 * cos(TAU / 16.0)) / sin(TAU / 16.0) * abs(sin(local)));
            float frame = max(abs(q.x), abs(q.y));
            float grain = noise(vec2(p.x * 49.0, p.y * 2.0)) + 0.4 * noise(vec2(p.x * 90.0, p.y * 3.0));
            result = mix(color1, color2, 0.18 + 0.24 * grain);
            result = mix(result, color3, coverage(abs(frame - 0.462), 0.007));
            result = mix(result, color2, coverage(abs(radius - 0.329), 0.017));
            result = mix(result, color4, coverage(abs(radius - 0.329), 0.005));
            vec4 veneer = local < 0.0 ? (mod(sector, 2.0) < 0.5 ? color4 : color3) : color2;
            float streak = noise(
                    vec2(dot(q, vec2(cos(angle - local), sin(angle - local))) * 6.0, sin(local) * radius * 103.0));
            veneer = mix(veneer, color1, streak * 0.14);
            result = mix(result, color1, coverage(radius, limit + 0.007));
            result = mix(result, veneer, coverage(radius, limit));
        """.trimIndent()

        PatternTextureType.WaffleIron -> """
            vec2 board = mat2(0.92388, -0.38268, 0.38268, 0.92388) * p;
            vec2 cell = floor(board), q = fract(board) - 0.5;
            float wall = pow(pow(abs(q.x), 7.0) + pow(abs(q.y), 7.0), 1.0 / 7.0);
            vec2 normal = normalize(sign(q) * pow(abs(q) + 0.001, vec2(6.0)) + vec2(0.0000001));
            float light = clamp(0.45 + 0.55 * dot(normal, vec2(-0.65, 0.76)), 0.0, 1.0);
            float toast = noise(p * 6.0) * 0.46 + noise(p * 24.0) * 0.28 + hash(cell) * 0.26;
            vec4 floorColor = mix(color1, color2, toast * 0.68);
            vec4 slope = mix(color4, color3, light);
            result = mix(floorColor, slope, smoothstep(0.21, 0.355, wall));
            float crumb = smoothstep(0.75, 0.9, noise(p * 38.0));
            result = mix(result, mix(color3, color2, toast), smoothstep(0.378, 0.465, wall));
            result = mix(result, color4, 0.28 * crumb);
        """.trimIndent()

        PatternTextureType.CitrusWheels -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 cell = floor(tile), q = fract(tile) - 0.5;
            float radius = length(q), angle = safeAngle(q) + TAU * hash(cell);
            float wedges = 8.0 + floor(hash(cell + 1.0) * 3.0);
            float wedge = floor(angle / TAU * wedges);
            vec4 fruit = mix(color3, color4, 0.76 * hash(cell + 8.0));
            float rind = 0.429 + 0.004 * sin(angle * 19.0);
            result = mix(color1, fruit, coverage(radius, rind));
            result = mix(result, color2, coverage(radius, rind - 0.027));
            float seam = abs(sin(angle * wedges * 0.5)) * radius / (0.5 * wedges);
            float pulp = coverage(radius, rind - 0.06) * (1.0 - coverage(seam, 0.009));
            vec4 flesh = mix(fruit, color2, 0.08 + 0.19 * hash(vec2(wedge, cell.y)));
            float sacs = noise(vec2(radius * 41.0, angle * 33.0));
            flesh = mix(flesh, color2, 0.33 * smoothstep(0.48, 0.82, sacs));
            result = mix(result, flesh, pulp);
            result = mix(result, color2, coverage(radius, 0.028));
            float seedAngle = (fract(angle / TAU * wedges) - 0.46) * TAU / wedges;
            vec2 pip = vec2((radius - 0.245) / 0.04, seedAngle* radius / 0.013);
            float pipMask = coverage(length(pip) * 0.018, 0.018) * step(0.78, hash(cell + wedge));
            result = mix(result, color2, pipMask);
        """.trimIndent()

        PatternTextureType.SaltedPretzels -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 cell = floor(tile), q = fract(tile) - 0.5;
            float turn = 0.45 * (hash(cell) - 0.5);
            q = mat2(cos(turn), -sin(turn), sin(turn), cos(turn)) * q;
            float dough = 2.0;
            vec2 previous = vec2(-0.24, 0.21);
            for (int i = 1; i <= 38; i++) {
                float t = float(i) / 38.0;
                vec2 nextPoint =
                        vec2(0.37 * sin(TAU * (1.35 * t - 0.175)), -0.025 - 0.23 * cos(TAU * (1.83 * t + 0.085)));
                if (i == 1) previous = vec2(0.37 * sin(-TAU * 0.175), -0.025 - 0.23 * cos(TAU * 0.085));
                dough = min(dough, segment(q, previous, nextPoint));
                previous = nextPoint;
            }
            result = mix(color1, color2, coverage(dough, 0.076));
            float bulge = clamp(1.0 - dough / 0.068, 0.0, 1.0);
            vec4 crust = mix(color2, color3, 0.38 + 0.58 * sqrt(bulge));
            crust = mix(crust, color2, 0.23 * noise(q * 32.0 + cell));
            result = mix(result, crust, coverage(dough, 0.065));
            vec2 grain = q * 25.0;
            vec2 salt = fract(grain) - (0.22 + 0.56 * hash2(floor(grain) + cell));
            float crystal = coverage((abs(salt.x) + abs(salt.y)) / 25.0, 0.012);
            result = mix(result, color4, crystal* coverage(dough, 0.054) * step(0.69, hash(floor(grain))));
        """.trimIndent()

        PatternTextureType.LiquoriceAllsorts -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 cell = floor(tile), q = fract(tile) - 0.5;
            float angle = (hash(cell) - 0.5) * 1.52;
            q = mat2(cos(angle), -sin(angle), sin(angle), cos(angle)) * q;
            float shape = hash(cell + 4.0);
            vec4 fondant = hash(cell + 9.0) > 0.5 ? color3 : color4;
            result = color1;
            if (shape < 0.48) {
                vec2 corner = abs(q) - vec2(0.294, 0.176);
                float edge = length(max(corner, 0.0)) + min(max(corner.x, corner.y), 0.0);
                float bands = abs(fract((q.y + 0.1) * 9.5) - 0.5);
                vec4 layer = mix(fondant, color2, coverage(bands / 9.5, 0.019));
                layer = mix(layer, color1, 0.23 * smoothstep(0.17, 0.31, -q.x));
                result = mix(result, layer, coverage(edge, 0.033));
            } else if (shape < 0.75) {
                float r = length(q * vec2(1.0, 1.15));
                result = mix(result, color2, coverage(r, 0.31));
                result = mix(result, fondant, coverage(r, 0.178));
                result = mix(result, color1, 0.28 * coverage(length(q - vec2(-0.065, 0.076)), 0.043));
            } else {
                float r = length(q);
                vec2 sprinkles = q * 18.0;
                vec2 dotPos = fract(sprinkles) - 0.25 - 0.5 * hash2(floor(sprinkles));
                vec4 coat = mix(fondant, color2, 0.22);
                coat = mix(coat, mix(fondant, color1, 0.47), coverage(length(dotPos) / 18.0, 0.021));
                result = mix(result, coat, coverage(r, 0.272));
            }
        """.trimIndent()

        PatternTextureType.RavioliStamp -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 cell = floor(tile), q = fract(tile) - 0.5;
            float angle = 0.42 * (hash(cell) - 0.5);
            q = mat2(cos(angle), -sin(angle), sin(angle), cos(angle)) * q;
            vec2 corner = abs(q) - 0.304;
            float distance = length(max(corner, 0.0)) + min(max(corner.x, corner.y), 0.0);
            float along = abs(q.x) > abs(q.y) ? q.y : q.x;
            float crimp = 0.5 + 0.5 * cos(along * TAU * 10.5);
            float edge = 0.063 + 0.016 * crimp;
            result = mix(color1, color4, coverage(distance, edge + 0.007));
            vec4 dough = mix(color2, color3, 0.35 + noise(q * 23.0 + cell) * 0.15);
            float rim = smoothstep(-0.047, 0.021, distance);
            dough = mix(dough, color4, 0.28 * crimp * rim);
            float pillow = length(q * vec2(1.0, 1.1));
            dough = mix(dough, color3, 0.87 * (1.0 - smoothstep(0.135, 0.234, pillow)));
            dough = mix(dough, color2, 0.78 * (1.0 - smoothstep(0.045, 0.195, length(q - vec2(-0.049, 0.041)))));
            result = mix(result, dough, coverage(distance, edge));
        """.trimIndent()

        PatternTextureType.CameoBrocade -> """
            float band = p.y * 0.85;
            vec2 tile = vec2(p.x + 0.5 * floor(band), band);
            vec2 cell = floor(tile), q = fract(tile) - 0.5;
            q.x *= 1.18;
            float oval = length(q * vec2(1.09, 0.92));
            float angle = safeAngle(q * vec2(1.09, 0.92));
            float bead = 0.018 * cos(angle * 26.0);
            result = mix(color1, color3, coverage(oval, 0.366 + bead));
            result = mix(result, color1, coverage(oval, 0.334));
            result = mix(result, color4, coverage(oval, 0.318));
            result = mix(result, color2, coverage(oval, 0.304));
            q.x *= mod(cell.x + cell.y, 2.0) < 1.0 ? -1.0 : 1.0;
            float head = length((q - vec2(-0.017, -0.087)) * vec2(1.0, 0.94)) - 0.108;
            float bun = length((q - vec2(-0.124, -0.092)) * vec2(1.0, 1.18)) - 0.053;
            float face = length((q - vec2(0.038, -0.041)) * vec2(1.0, 0.76)) - 0.061;
            float nose = segment(q, vec2(0.081, -0.073), vec2(0.119, -0.031)) - 0.010;
            float neck = segment(q, vec2(-0.018, -0.011), vec2(-0.043, 0.103)) - 0.041;
            float shoulder = length((q - vec2(-0.043, 0.166)) * vec2(0.64, 1.0)) - 0.075;
            float profile = min(min(head, bun), min(face, min(nose, min(neck, shoulder))));
            result = mix(result, color3, coverage(profile, 0.007));
            result = mix(result, color4, coverage(profile, 0.0));
            float hair = length((q - vec2(-0.020, -0.120)) * vec2(0.89, 1.35));
            float wave = abs(hair - 0.079);
            result = mix(result, color3,
                         0.68 * coverage(wave, 0.005) * coverage(q.x, 0.013) * coverage(profile, -0.013));
            float collar = abs(length((q - vec2(-0.004, 0.065)) * vec2(0.83, 1.6)) - 0.078);
            result = mix(result, color3,
                         0.90 * coverage(collar, 0.006) * coverage(profile, -0.012) * step(0.080, q.y));
        """.trimIndent()

        PatternTextureType.BowTiePatchwork -> """
            vec2 tile = floor(p), q = fract(p) - 0.5;
            if (mod(tile.x + tile.y, 2.0) < 1.0) q = vec2(-q.y, q.x);
            float crossed = abs(q.y) - 0.72 * abs(q.x) - 0.051;
            vec4 fabric = mod(tile.x, 2.0) < 1.0 ? color2 : color3;
            float grain = 0.5 + 0.5 * cos((q.x - q.y) * TAU * 17.0);
            result = mix(color1, fabric, coverage(crossed, 0.0));
            result = mix(result, color1, 0.16 * grain);
            float seam = abs(crossed + 0.038);
            float dash = 1.0 - smoothstep(0.21, 0.39, abs(fract(abs(q.x) * 16.0) - 0.5));
            result = mix(result, color1, 0.8 * dash * coverage(seam, 0.008));
            float knot = abs(q.x) + 0.82 * abs(q.y);
            result = mix(result, color4, coverage(knot, 0.163));
            float hem = min(0.5 - abs(q.x), 0.5 - abs(q.y));
            result = mix(result, mix(fabric, color1, 0.38), coverage(hem, 0.006));
        """.trimIndent()

        PatternTextureType.TasselTrim -> """
            vec2 tile = vec2(p.x * 1.65 + 0.5 * floor(p.y), p.y);
            vec2 cell = floor(tile), q = vec2(fract(tile.x) - 0.5, fract(tile.y));
            q.x /= 1.65;
            vec4 silk = mod(cell.x + cell.y, 3.0) < 1.0 ? color2 : (mod(cell.x, 2.0) < 1.0 ? color3 : color4);
            float ribbon = abs(q.y - 0.12);
            result = mix(color1, color2, coverage(ribbon, 0.072));
            float braid = abs(ribbon - 0.038 * sin(p.x * TAU * 5.0));
            result = mix(result, color3, coverage(braid, 0.013));
            float cord = segment(q, vec2(0.0, 0.17), vec2(0.0, 0.372));
            result = mix(result, color4, coverage(cord, 0.014));
            float t = clamp((q.y - 0.397) / 0.43, 0.0, 1.0);
            float strand = 10.0;
            for (int i = -6; i <= 6; i++) {
                float n = float(i) / 6.0;
                float x = n * (0.043 + 0.139 * pow(t, 0.84)) + 0.025 * sin(t * 3.4);
                float tip = 0.835 + 0.036 * cos(n * 2.3);
                float d = max(abs(q.x - x), max(0.393 - q.y, q.y - tip));
                strand = min(strand, d);
            }
            result = mix(result, silk, coverage(strand, 0.011));
            float binding = length((q - vec2(0.0, 0.389)) * vec2(1.0, 1.45));
            result = mix(result, color3, coverage(binding, 0.072));
            result = mix(result, color4, coverage(binding, 0.046));
        """.trimIndent()

        PatternTextureType.MillefioriCanes -> """
            vec2 grid = vec2(p.x, p.y / 0.866);
            vec2 base = floor(grid);
            vec2 local = vec2(0.0), chosen = base;
            float nearest = 2.0;
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 cell = base + vec2(float(x), float(y));
                    vec2 center = vec2(cell.x + mod(cell.y, 2.0) * 0.5, cell.y * 0.866);
                    vec2 v = p - center - (hash2(cell) - 0.5) * 0.06;
                    if (dot(v, v) < nearest) {
                        nearest = dot(v, v);
                        local = v;
                        chosen = cell;
                    }
                }
            float radius = length(local), angle = safeAngle(local);
            float petals = 5.0 + floor(4.0 * hash(chosen + 5.0));
            float petal = 0.5 + 0.5 * cos(angle * petals + TAU * hash(chosen));
            vec4 mantle = hash(chosen + 3.0) < 0.52 ? color2 : color3;
            vec4 flower = hash(chosen + 3.0) < 0.52 ? color3 : color2;
            result = mix(color1, mantle, coverage(radius, 0.454));
            result = mix(result, color4, coverage(radius, 0.389));
            result = mix(result, color1, coverage(radius, 0.359));
            result = mix(result, flower, coverage(radius, 0.171 + 0.157 * petal));
            result = mix(result, color4, coverage(radius, 0.092 + 0.115 * petal));
            result = mix(result, mantle, coverage(radius, 0.084));
        """.trimIndent()

        PatternTextureType.GreekMeander -> """
            vec2 q = fract(p);
            float row = mod(floor(p.y), 2.0);
            if (row > 0.5) q.x = 1.0 - q.x;
            float key = segment(q, vec2(0.0, 0.74), vec2(0.21, 0.74));
            key = min(key, segment(q, vec2(0.21, 0.74), vec2(0.21, 0.25)));
            key = min(key, segment(q, vec2(0.21, 0.25), vec2(0.81, 0.25)));
            key = min(key, segment(q, vec2(0.81, 0.25), vec2(0.81, 0.60)));
            key = min(key, segment(q, vec2(0.81, 0.60), vec2(0.43, 0.60)));
            key = min(key, segment(q, vec2(0.43, 0.60), vec2(0.43, 0.405)));
            key = min(key, segment(q, vec2(0.43, 0.405), vec2(0.64, 0.405)));
            key = min(key, segment(q, vec2(0.21, 0.74), vec2(1.0, 0.74)));
            float border = min(q.y, 1.0 - q.y);
            result = mix(color1, color3, coverage(border, 0.089));
            result = mix(result, color4, coverage(abs(border - 0.121), 0.014));
            result = mix(result, row < 0.5 ? color2 : color4, coverage(key, thickness));
            float beads = length(vec2((fract(q.x * 5.0) - 0.5) / 5.0, border));
            result = mix(result, color2, coverage(beads, 0.032));
        """.trimIndent()

        PatternTextureType.PaperChainGarland -> """
            float row = floor(p.y), stagger = 0.48 * mod(row, 2.0);
            vec2 q = vec2(p.x + stagger, fract(p.y) - 0.49);
            float base = floor(q.x * 2.0);
            result = color1;
            for (int pass = 0; pass < 2; pass++) {
                for (int i = -2; i <= 2; i++) {
                    float link = base + float(i);
                    if (mod(link, 2.0) != float(pass)) continue;
                    float center = 0.5 * link;
                    float sag = 0.19 * cos(center * TAU / 3.5);
                    float slope = -0.19 * TAU / 3.5 * sin(center * TAU / 3.5);
                    vec2 v = vec2(q.x - center, q.y - sag);
                    v = mat2(1.0, -slope, slope, 1.0) * v / sqrt(1.0 + slope * slope);
                    float foreshortening = pass == 0 ? 1.36 : 3.0;
                    vec2 ellipse = v * vec2(1.0, foreshortening);
                    float dist = abs(length(ellipse) - 0.304) /
                                 length(normalize(ellipse + 0.00001) * vec2(1.0, foreshortening));
                    float choice = mod(link + row, 3.0);
                    vec4 paper = choice < 1.0 ? color2 : (choice < 2.0 ? color3 : color4);
                    float light = 0.2 + 0.7 * smoothstep(-0.31, 0.24, v.x);
                    paper = mix(paper, color1, (1.0 - light) * 0.34);
                    result = mix(result, paper, coverage(dist, pass == 0 ? 0.039 : 0.031));
                }
            }
        """.trimIndent()

        PatternTextureType.RibbonCockade -> """
            vec2 tile = vec2(p.x + 0.5 * floor(p.y), p.y);
            vec2 cell = floor(tile), q = fract(tile) - 0.5;
            q.y += 0.074;
            vec4 ribbon = mod(cell.x + cell.y, 2.0) < 1.0 ? color2 : color3;
            vec4 center = mod(cell.x + cell.y, 2.0) < 1.0 ? color3 : color2;
            result = color1;
            float tailX = abs(q.x) - (0.057 + 0.36 * q.y);
            float tail = max(abs(tailX) - 0.066, max(0.066 - q.y, q.y - 0.412 - abs(tailX) * 0.49));
            result = mix(result, ribbon, coverage(tail, 0.0));
            result = mix(result, color4, coverage(abs(tailX - 0.036), 0.008) * coverage(tail, 0.0));
            float a = safeAngle(q), r = length(q);
            float folds = fract(a / TAU * 17.0);
            float edge = 0.302 + 0.031 * (1.0 - abs(2.0 * folds - 1.0));
            vec4 fan = mix(ribbon, color1, 0.41 * (1.0 - folds));
            fan = mix(fan, color4, 0.27 * smoothstep(0.78, 1.0, folds));
            result = mix(result, fan, coverage(r, edge));
            result = mix(result, color4, coverage(r, 0.144));
            result = mix(result, center, coverage(r, 0.124));
            float star = 0.050 + 0.020 * cos(5.0 * a);
            result = mix(result, color4, coverage(r, star));
        """.trimIndent()

        PatternTextureType.VenetianShutters -> """
            vec2 q = fract(p * vec2(1.0, 0.7));
            float slat = fract(q.y * 9.0);
            float bevel = smoothstep(0.08, 0.82, slat);
            result = mix(color2, color3, bevel);
            result = mix(result, color1, shadows * (1.0 - smoothstep(0.0, 0.23, slat)));
            result = mix(result, color4, coverage(abs(slat - 0.88) / 9.0, 0.006) * 0.7);
            float frame = min(min(q.x, 1.0 - q.x), min(q.y, 1.0 - q.y));
            result = mix(result, color1, coverage(frame, 0.065));
            result = mix(result, color2, coverage(frame, 0.047));
            result = mix(result, color3, coverage(abs(frame - 0.042), 0.005));
            float pin = length(vec2(min(q.x, 1.0 - q.x) - 0.024, abs(q.y - 0.5) - 0.34));
            result = mix(result, color4, coverage(pin, 0.010));
        """.trimIndent()

        PatternTextureType.SandstoneArcade -> """
            vec2 q = fract(p * vec2(1.0, 0.76)) - 0.5;
            vec2 arch = vec2(q.x, min(q.y + 0.01, 0.0));
            float opening = length(arch);
            float courses = fract((q.y + 0.5) * 7.0);
            float seam = min(courses, 1.0 - courses) / 7.0;
            vec2 block = floor(vec2(p.x * 2.0 + 0.5 * mod(floor((q.y + 0.5) * 7.0), 2.0), p.y * 5.32));
            result = mix(color2, color3, 0.48 + 0.4 * hash(block));
            result = mix(result, color2, coverage(seam, 0.004) * 0.72);
            float band = coverage(opening, radius + 0.10);
            float joints = abs(fract(safeAngle(arch) * 13.0 / 3.141593 + 0.5) - 0.5);
            vec4 stone = mix(color4, color2, coverage(joints* max(opening, 0.01) * 0.24, 0.006) * step(q.y, -0.01));
            result = mix(result, stone, band);
            result = mix(result, mix(color1, color2, 0.18 + 0.21 * q.y), coverage(opening, radius));
            float sill = max(abs(q.y - 0.432) - 0.022, abs(q.x) - 0.48);
            result = mix(result, color4, coverage(sill, 0.0));
        """.trimIndent()

        PatternTextureType.GearBlueprint -> """
            vec2 q = fract(p + 0.5) - 0.5;
            vec2 grid = abs(fract(p * 10.0 + 0.5) - 0.5) / 10.0;
            result = mix(color1, color2, 0.38 * coverage(min(grid.x, grid.y), 0.002));
            float a = safeAngle(q);
            float r = length(q);
            float teeth = smoothstep(-0.48, 0.48, cos(a * 18.0));
            float profile = 0.296 + 0.041 * teeth;
            float rim = min(min(abs(r - profile), abs(r - 0.245)), abs(r - 0.066));
            float spokes = abs(sin(a * 3.0)) * r;
            float spokeEdge = abs(spokes - 0.016);
            rim = min(rim, max(spokeEdge, max(0.073 - r, r - 0.239)));
            result = mix(result, color3, coverage(rim, thickness));
            float center = min(abs(q.x), abs(q.y));
            float dash = step(0.2, fract(max(abs(q.x), abs(q.y)) * 18.0));
            result = mix(result, color4, coverage(center, thickness * 0.54) * dash);
            float corners = min(segment(abs(q), vec2(0.385, 0.385), vec2(0.385, 0.34)),
                                segment(abs(q), vec2(0.385, 0.385), vec2(0.34, 0.385)));
            result = mix(result, color2, coverage(corners, thickness));
        """.trimIndent()

        PatternTextureType.AdmissionTickets -> """
            vec2 row = p * vec2(0.78, 1.65);
            row.x += 0.33 * mod(floor(row.y), 2.0);
            vec2 cell = floor(row), q = fract(row) - 0.5;
            vec2 edge = vec2(0.465, 0.415) - abs(q);
            vec2 notch = vec2(abs(q.x) - 0.465, q.y);
            float paper = min(min(edge.x, edge.y), length(notch) - radius);
            vec4 stock = mod(cell.x + cell.y, 3.0) < 1.0 ? color2 : color3;
            result = mix(color1, stock, 1.0 - coverage(paper, 0.0));
            float rule = abs(max(abs(q.x + 0.081) - 0.291, abs(q.y) - 0.33));
            float perf = abs(q.x - 0.255);
            float printMask = max(coverage(rule, 0.008), coverage(perf, 0.006) * step(0.42, fract(q.y * 16.0)));
            float rose = length((q + vec2(0.08, 0.0)) * vec2(1.0, 0.64));
            float burst = 0.132 + 0.020 * cos(10.0 * safeAngle((q + vec2(0.08, 0.0)) * vec2(1.0, 0.64)));
            printMask = max(printMask, max(coverage(abs(rose - burst), 0.007), coverage(rose, 0.077)));
            float code = step(0.03, q.x - 0.25) * step(abs(q.y), 0.26) *
                         step(0.36, hash(vec2(floor(q.y * 42.0), cell.x)));
            result = mix(result, color4, max(printMask, code* step(q.x, 0.39)) * (1.0 - coverage(paper, 0.0)));
        """.trimIndent()

        PatternTextureType.SawtoothRoofscape -> """
            vec2 q = p * vec2(1.2, 0.86);
            float y = fract(q.y), row = floor(q.y);
            float x = fract(q.x + row * 0.37);
            float peak = 0.22;
            float glass = max(0.0, (peak - x) / peak);
            float slope = max(0.0, (x - peak) / (1.0 - peak));
            float roof = 0.13 + 0.47 * (glass + slope);
            result = mix(color1, color2, 0.19 * smoothstep(0.0, 0.70, y));
            vec4 wall = mix(color3, color4, shadows * 0.32 * smoothstep(0.62, 1.0, y));
            float glazing = step(x, peak) * step(y, 0.605);
            wall = mix(wall, color2, glazing);
            float bars = abs(fract(x * 18.0) - 0.5) / 18.0;
            wall = mix(wall, color4, glazing* coverage(bars, 0.005));
            wall = mix(wall, color4, coverage(abs(y - roof), 0.018));
            float window = max(abs(fract(x * 5.0) - 0.5) - 0.29, abs(y - 0.81) * 2.5 - 0.16);
            wall = mix(wall, color4, coverage(window, 0.0));
            result = mix(result, wall, smoothstep(roof - pixelWidth, roof + pixelWidth, y));
        """.trimIndent()

        PatternTextureType.FanVault -> """
            vec2 q = fract(p) - 0.5;
            vec2 fan = 0.5 - abs(q);
            float r = length(fan);
            float a = safeAngle(fan);
            float ray = abs(sin(a * 7.0)) * r / 7.0;
            float rib = min(ray, abs(r - 0.5));
            float ashlar = abs(fract(r * 11.0 + 0.5) - 0.5) / 11.0;
            float shade = 0.36 + 0.28 * cos(a * 14.0) - 0.2 * r;
            result = mix(color1, color2, shade + 0.25);
            result = mix(result, color3, 0.22 * coverage(ashlar, 0.004));
            result = mix(result, color1, coverage(rib, thickness * 1.9));
            result = mix(result, color3, coverage(rib, thickness));
            result = mix(result, color4, coverage(rib, thickness * 0.39));
            float boss = length(q);
            result = mix(result, color3, coverage(boss, 0.072));
            result = mix(result, color4, coverage(abs(boss - 0.045), thickness * 0.5));
        """.trimIndent()

        PatternTextureType.WoodcutSwells -> """
            vec2 q = p * vec2(0.72, 1.28);
            float phase = q.x + 0.10 * sin(q.y * 2.8);
            float curl = 0.28 * sin(TAU * phase) + 0.09 * sin(TAU * (2.0 * phase + 0.08));
            float depth = fract(q.y - curl);
            float ridge = abs(fract(depth * 6.0 + 0.3 * sin(TAU * phase + depth * 2.3)) - 0.5) / 6.0;
            result = mix(color2, color3, smoothstep(0.03, 0.95, depth));
            result = mix(result, color1, coverage(ridge, thickness* exp(-2.25 * depth)));
            result = mix(result, color4, coverage(abs(depth - 0.048), thickness * 0.58));
            result = mix(result, color1, coverage(depth, thickness * 0.80));
        """.trimIndent()

        PatternTextureType.ApartmentPlan -> """
            vec2 q = fract(p) - 0.5;
            if (mod(floor(p.x) + floor(p.y), 2.0) > 0.5) q.x = -q.x;
            vec2 edge = 0.5 - abs(q);
            result = mix(color1, color3, 0.10 * step(0.74, fract((q.x - q.y) * 38.0)));
            float walls = min(min(edge.x, edge.y),
                              min(max(abs(q.x + 0.04), 0.22 - q.y), max(abs(q.x + 0.04), q.y - 0.03)));
            walls = min(walls, max(abs(q.y + 0.09), max(-0.03 - q.x, 0.12 - abs(q.x - 0.25))));
            result = mix(result, color2, coverage(walls, thickness));
            vec2 door = q - vec2(-0.04, 0.03);
            float swing = max(abs(length(door) - 0.19), max(-door.y, -door.x));
            result = mix(result, color3, coverage(swing, thickness * 0.36));
            result = mix(result, color2,
                         coverage(segment(q, vec2(-0.04, 0.03), vec2(0.15, 0.03)), thickness * 0.46));
            vec2 furniture = q - vec2(-0.30, -0.22);
            float bed = max(abs(furniture.x) - 0.105, abs(furniture.y) - 0.20);
            result = mix(result, color4, coverage(abs(bed), thickness * 0.45));
            result = mix(result, color4,
                         coverage(segment(furniture, vec2(-0.104, -0.10), vec2(0.104, -0.10)), thickness * 0.4));
            float window = max(edge.y, abs(q.x - 0.24) - 0.13);
            result = mix(result, color4, coverage(window, thickness * 0.52));
        """.trimIndent()

        PatternTextureType.RooftopTanks -> """
            vec2 row = p * vec2(1.05, 0.74);
            row.x += mod(floor(row.y), 2.0) * 0.51;
            vec2 tile = floor(row), q = fract(row) - 0.5;
            float lift = (hash(tile) - 0.5) * 0.14;
            q.y -= lift;
            result = mix(color1, color2, smoothstep(-0.48, 0.38, q.y));
            float r = 0.228;
            float roof = max(abs(q.x) - r - 0.026, max(-0.33 + abs(q.x) * 0.44 - q.y, q.y + 0.206));
            float drum = max(abs(q.x) - r, max(-0.211 - q.y, q.y - 0.103 + 0.035 * q.x * q.x / (r * r)));
            float timber = abs(fract(q.x * 28.0) - 0.5) / 28.0;
            vec4 stave = mix(color3, color4, 0.3 * shadows * (1.0 - q.x / r));
            stave = mix(stave, color4, coverage(timber, 0.003) * 0.58);
            float hoops = min(abs(q.y + 0.154), abs(q.y - 0.038));
            stave = mix(stave, color4, coverage(hoops, 0.008));
            float legs = min(segment(vec2(abs(q.x), q.y), vec2(0.14, 0.087), vec2(0.20, 0.38)),
                             segment(q, vec2(-0.13, 0.13), vec2(0.17, 0.35)));
            legs = min(legs, segment(q, vec2(0.13, 0.13), vec2(-0.17, 0.35)));
            result = mix(result, color4, coverage(legs, 0.015));
            result = mix(result, stave, coverage(drum, 0.0));
            result = mix(result, color4, coverage(roof, 0.0));
            float cornice = q.y - 0.357 - 0.025 * step(0.25, abs(q.x));
            result = mix(result, color4, smoothstep(-pixelWidth, pixelWidth, cornice));
        """.trimIndent()

        PatternTextureType.BookclothSpines -> """
            vec2 shelf = p * vec2(2.65, 0.87);
            shelf.x += 0.43 * floor(shelf.y);
            vec2 book = floor(shelf), q = fract(shelf);
            float height = 0.055 + 0.16 * hash(book);
            float bevel = 1.0 - smoothstep(0.0, 0.32, min(q.x, 1.0 - q.x));
            vec4 cloth = mix(color1, color2, hash(book + 3.0));
            float grain = noise(vec2(q.x * 52.0, q.y * 135.0));
            result = mix(cloth, color3, 0.07 * grain);
            result.rgb *= 1.0 - bevel * shadows * 0.53;
            float head = q.y - height;
            float fillet = min(min(abs(head - 0.038), abs(head - 0.052)), min(abs(q.y - 0.86), abs(q.y - 0.879)));
            result = mix(result, color3, coverage(fillet, 0.0035));
            vec2 badge = vec2((q.x - 0.5) / 2.65, head - 0.27);
            float frame = abs(max(abs(badge.x) - 0.125, abs(badge.y) - 0.116));
            result = mix(result, color3, coverage(frame, 0.004));
            float fleuron = abs(badge.x) + abs(badge.y) * 0.57;
            result = mix(result, color4, coverage(abs(fleuron - 0.035), 0.0045));
            float seam = min(q.x, 1.0 - q.x) / 2.65;
            result = mix(result, color1, max(coverage(head, 0.0), coverage(seam, 0.006)));
            result = mix(result, color3, smoothstep(0.944, 0.955, q.y));
        """.trimIndent()

        PatternTextureType.HarbourBunting -> """
            vec2 row = p * vec2(1.65, 1.20);
            float y = row.y - 0.12 * cos(row.x * 2.2);
            vec2 cell = floor(vec2(row.x, y));
            vec2 q = fract(vec2(row.x, y)) - 0.5;
            float bend = intensity * 0.052 * sin(q.y * 6.0 + 1.3 * cell.x);
            vec2 flag = q - vec2(bend, 0.035 * cos(q.x * 8.0 + cell.y));
            result = mix(color1, color4, 0.36 * noise(p * 2.5));
            result = mix(result, color3, coverage(abs(q.y + 0.262), 0.009));
            float side = 0.273 - 0.095 * (flag.y + 0.17);
            float cut = max(abs(flag.x) - side, max(-0.246 - flag.y, flag.y - 0.265));
            float style = mod(cell.x + 2.0 * cell.y, 4.0);
            vec4 cloth = color4;
            if (style < 1.0)
                cloth = mix(color4, color2, coverage(abs(flag.x - flag.y), 0.10));
            else if (style < 2.0)
                cloth = mix(color2, color3, step(0.0, flag.x* flag.y));
            else if (style < 3.0)
                cloth = mix(color3, color4, coverage(min(abs(flag.x), abs(flag.y)), 0.046));
            else
                cloth = mix(color4, color2, coverage(length(flag), 0.128));
            cloth.rgb *= 0.91 + 0.09 * cos(flag.x * 11.0 + flag.y * 2.0);
            result = mix(result, cloth, coverage(cut, 0.0));
        """.trimIndent()

        PatternTextureType.PantographLinkage -> """
            vec2 q = fract(p + 0.5) - 0.5;
            float tilt = mod(floor(p.y + 0.5), 2.0) * 2.0 - 1.0;
            q.y *= tilt;
            vec2 pivot = vec2(-0.37, -0.24), left = vec2(-0.21, 0.17);
            vec2 upper = vec2(0.21, -0.18), lower = vec2(-0.01, 0.24);
            vec2 tip = vec2(0.39, -0.11);
            vec2 ruling = abs(fract(p * 10.0 + 0.5) - 0.5) / 10.0;
            result = mix(color1, color3, 0.13 * coverage(min(ruling.x, ruling.y), 0.002));
            float longBar = min(segment(q, pivot, left), segment(q, left, upper));
            float crossbar = min(segment(q, left, lower), segment(q, lower, tip));
            crossbar = min(crossbar, segment(q, upper, tip));
            result = mix(result, color2, coverage(longBar, thickness));
            result = mix(result, color3, coverage(crossbar, thickness * 0.75));
            float bolts = min(min(length(q - pivot), length(q - upper)), min(length(q - left), length(q - lower)));
            result = mix(result, color4, coverage(bolts, thickness * 1.27));
            result = mix(result, color3, coverage(bolts, thickness * 0.45));
            result = mix(result, color4, coverage(length(q - tip), thickness * 0.86));
        """.trimIndent()

        PatternTextureType.DualTrace -> """
            vec2 grid = abs(fract(p * 8.0 + 0.5) - 0.5) / 8.0;
            float axes = min(abs(fract(p.x + 0.5) - 0.5), abs(fract(p.y + 0.5) - 0.5));
            result = mix(color1, color2, 0.70 * coverage(min(grid.x, grid.y), 0.0012));
            result = mix(result, color2, coverage(axes, 0.0024));
            for (int i = 0; i < 2; i++) {
                float channel = float(i);
                float phase = TAU * (p.x * (1.7 + 0.55 * channel) + 0.12 * channel);
                float signal = 0.14 * sin(phase) + 0.025 * sin(3.0 * phase);
                float slope = TAU * (1.7 + 0.55 * channel) * (0.14 * cos(phase) + 0.075 * cos(3.0 * phase));
                float scan = abs(fract(p.y - channel * 0.40 - signal + 0.5) - 0.5) / sqrt(1.0 + slope * slope);
                vec4 ink = i == 0 ? color3 : color4;
                result = mix(result, ink, exp(-scan * 92.0) * intensity * 0.44);
                result = mix(result, ink, coverage(scan, 0.004));
            }
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
            float weather = noise(p * 0.28);
            float isoline = abs(fract(weather * 9.0 + 0.5) - 0.5);
            result = mix(color1, color2, weather * 0.75 + 0.22 * coverage(isoline, 0.029));
            vec2 row = vec2(p.x + 0.5 * mod(floor(p.y + 0.5), 2.0), p.y);
            vec2 station = floor(row + 0.5), local = fract(row + 0.5) - 0.5;
            float angle = noise(station * 0.23) * TAU + variability * sin(station.x * 0.6);
            vec2 q = mat2(cos(angle), -sin(angle), sin(angle), cos(angle)) * local;
            float d = segment(q, vec2(-0.24, 0.0), vec2(0.27, 0.0));
            float feathers = 1.0 + floor(hash(station) * 4.0);
            for (int i = 0; i < 4; i++) {
                if (float(i) >= feathers) break;
                float x = 0.27 - float(i) * 0.077;
                d = min(d, segment(q, vec2(x, 0.0), vec2(x - 0.09, 0.132)));
            }
            result = mix(result, color3, coverage(d, 0.015));
            float ring = length(q + vec2(0.24, 0.0));
            result = mix(result, color3, coverage(ring, 0.055));
            result = mix(result, color4, coverage(ring, 0.028));
        """.trimIndent()

        PatternTextureType.AlignmentNomogram -> """
            vec2 cell = floor(p + 0.5);
            vec2 q = fract(p + 0.5) - 0.5;
            float axis = 10.0, ticks = 10.0;
            for (int i = 0; i < 3; i++) {
                float n = float(i) - 1.0;
                float base = n * 0.30;
                float bend = n * 0.28 * q.y * q.y;
                float x = q.x - base - bend;
                axis = min(axis, abs(x));
                float line = abs(fract(q.y * 32.0 + 0.5) - 0.5) / 32.0;
                float isMajor = 1.0 - step(0.5, mod(floor(q.y * 32.0 + 0.5), 4.0));
                ticks = min(ticks, max(line, abs(x - 0.013) - (0.019 + 0.011 * isMajor)));
            }
            float mask = coverage(abs(q.y), 0.425);
            result = mix(color1, color2, coverage(ticks, 0.0026) * mask);
            result = mix(result, color3, coverage(axis, 0.0045) * mask);
            for (int i = 0; i < 3; i++) {
                float n = float(i);
                float a = (hash(cell + vec2(3.0, n)) - 0.5) * 0.66;
                float b = (hash(cell + vec2(7.0, n)) - 0.5) * 0.66;
                vec2 start = vec2(-0.30 - 0.28 * a * a, a), end = vec2(0.30 + 0.28 * b * b, b);
                result = mix(result, color4, coverage(segment(q, start, end), 0.0052) * (0.60 + 0.15 * n));
                result = mix(result, color4, coverage(min(length(q - start), length(q - end)), 0.012));
            }
        """.trimIndent()

        PatternTextureType.VacuumTriodes -> """
            vec2 id = floor(p + 0.5), q = fract(p + 0.5) - 0.5;
            float height = 0.11 + 0.052 * hash(id);
            float glass = length(vec2(q.x, max(abs(q.y - 0.01) - height, 0.0))) - 0.196;
            float halo = exp(-max(glass, 0.0) * 16.0);
            result = mix(color1, color2, halo * 0.16);
            result = mix(result, color2, coverage(abs(glass), 0.009));
            float base = max(abs(q.x) - 0.178, abs(q.y + height + 0.13) - 0.048);
            for (int i = 0; i < 4; i++) {
                float x = (float(i) - 1.5) * 0.067;
                float pin = segment(q, vec2(x, -height - 0.14), vec2(x, -height - 0.254));
                result = mix(result, color2, coverage(pin, 0.008));
            }
            result = mix(result, color2, coverage(base, 0.0));
            float mount = min(abs(q.x - 0.106), abs(q.x + 0.106));
            result = mix(result, color2, coverage(mount, 0.009) * coverage(abs(q.y), height + 0.09));
            float rack = abs(fract(q.y * 35.0 + q.x * 1.9 + 0.5) - 0.5) / 35.0;
            float screen = coverage(abs(q.x), 0.106) * coverage(abs(q.y), height);
            result = mix(result, color2, coverage(rack, 0.0035) * screen);
            float filament = abs(q.x - 0.048 * sin((q.y + height) * 35.0));
            float hot = exp(-filament * 37.0) * coverage(abs(q.y), height + 0.01);
            result = mix(result, color3, (0.42 + intensity * 0.45) * hot);
            result = mix(result, color4, hot* hot * 0.92);
            float glint = segment(q, vec2(-0.158, 0.10), vec2(-0.158, -0.045));
            result = mix(result, color4, coverage(glint, 0.004) * 0.54);
        """.trimIndent()

        PatternTextureType.BasePairs -> """
            vec2 id = floor(vec2(p.x + 0.5, 0.0));
            vec2 q = vec2(fract(p.x + 0.5) - 0.5, p.y);
            float phase = TAU * (q.y * 0.85 + 0.17 * id.x);
            float wave = 0.27 * sin(phase);
            float row = floor(q.y * 13.0 + 0.5);
            float center = row / 13.0;
            float width = abs(0.27 * sin(TAU * (center * 0.85 + 0.17 * id.x)));
            float join = segment(q, vec2(-width, center), vec2(width, center));
            vec4 left = mod(row, 3.0) < 1.0 ? color2 : color3;
            vec4 right = mod(row, 2.0) < 1.0 ? color4 : color2;
            result = mix(color1, q.x < 0.0 ? left : right, coverage(join, 0.012) * 0.88);
            for (int i = 0; i < 2; i++) {
                float front = cos(phase) > 0.0 ? 1.0 : -1.0;
                float side = i == 0 ? -front : front;
                float slope = 0.27 * TAU * 0.85 * cos(phase);
                float d = abs(q.x - side * wave) / sqrt(1.0 + slope * slope);
                vec4 strand = side > 0.0 ? color3 : color4;
                if (i == 0) strand = mix(color1, strand, 0.70);
                result = mix(result, color1, coverage(d, thickness + 0.009));
                result = mix(result, strand, coverage(d, thickness));
            }
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
            vec2 page = floor(p + 0.5);
            vec2 q = fract(p + 0.5) - 0.5;
            float column = floor(q.x * 9.0 + 0.5);
            float across = abs(q.x * 9.0 - column);
            float lane = 1.0 - smoothstep(0.28, 0.48, across);
            float wells = max(0.0, 1.0 - abs(q.y - 0.43) * 90.0) * lane;
            float light = 0.0;
            for (int i = 0; i < 14; i++) {
                float n = float(i);
                vec2 id = page * 23.0 + vec2(column, n);
                float reference = 0.375 - 0.795 * pow(n / 13.0, 1.48);
                float marker = step(3.5, abs(column));
                float level = reference + (hash(id) - 0.5) * (1.0 - marker) * 0.018;
                float present = mix(step(0.48, hash(id + 9.8)), 1.0, marker);
                float dist = (q.y - level - 0.013 * across * across) / (0.0055 + 0.003 * n / 13.0);
                light = max(light, lane * present * exp(-dist * dist) * (0.42 + 0.58 * hash(id + 2.3)));
            }
            result = mix(color1, color2, 0.20 + (0.24 + 0.14 * q.y) * lane + 0.55 * wells);
            result = mix(result, color3, smoothstep(0.04, 0.58, light));
            result = mix(result, color4, smoothstep(0.48, 0.92, light));
        """.trimIndent()

        PatternTextureType.Stereonet -> """
            vec2 cell = floor(p + 0.5), q = (fract(p + 0.5) - 0.5) / 0.435;
            float r = length(q), square = dot(q, q);
            vec3 sphere = vec3(2.0 * q.x, 2.0 * q.y, 1.0 - square) / (1.0 + square);
            float longitude = atan(sphere.x, max(sphere.z, 0.0001));
            float latitude = asin(clamp(sphere.y, -1.0, 1.0));
            float lines = count / TAU;
            float meridian = abs(fract(longitude * lines + 0.5) - 0.5) * max(cos(latitude), 0.05);
            float parallel = abs(fract(latitude * lines + 0.5) - 0.5);
            float grid = min(meridian, parallel) / (lines * 4.4);
            result = mix(color1, color3, coverage(r, 1.0));
            result = mix(result, color2, coverage(grid, 0.0034) * coverage(r, 1.0));
            result = mix(result, color4, coverage(abs(q.x * 0.65 + sphere.z * 0.36), 0.014) * coverage(r, 1.0));
            result = mix(result, color3, coverage(abs(r - 1.055), 0.010));
        """.trimIndent()

        PatternTextureType.PressureDials -> """
            vec2 id = floor(p + 0.5), q = fract(p + 0.5) - 0.5;
            float radius = length(q);
            float angle = safeAngle(q);
            float sweep = mod(angle + TAU * 0.62, TAU);
            float arc = 1.0 - step(TAU * 0.75, sweep);
            float division = sweep / (TAU * 0.75) * 30.0;
            float tick = abs(fract(division + 0.5) - 0.5);
            float major = 1.0 - step(0.5, mod(floor(division + 0.5), 5.0));
            float tickmark = coverage(tick * radius * TAU * 0.75 / 30.0, 0.005);
            result = mix(color1, color2, coverage(radius, 0.454));
            result = mix(result, color4, coverage(radius, 0.426));
            result = mix(result, color3, arc* step(22.5, division) * coverage(abs(radius - 0.374), 0.018));
            result = mix(result, color2,
                         arc* tickmark* step(0.31 - major * 0.042, radius) * coverage(radius, 0.377));
            float pointer = -TAU * 0.62 + TAU * 0.75 * (0.13 + 0.74 * hash(id));
            vec2 direction = vec2(cos(pointer), sin(pointer));
            result = mix(result, color3, coverage(segment(q, -0.063 * direction, 0.30 * direction), 0.009));
            result = mix(result, color2, coverage(radius, 0.036));
        """.trimIndent()

        PatternTextureType.AirfoilStreamlines -> """
            vec2 q = fract(p + 0.5) - 0.5;
            float chord = clamp((q.x + 0.32) / 0.64, 0.0, 1.0);
            float thickness = 0.18 * (1.50 * sqrt(chord) - 1.08 * chord - 0.42 * chord * chord);
            float camber = 0.066 * sin(chord * 3.141593);
            float longitudinal = (q.x + 0.07) / 0.25;
            float wake = exp(-longitudinal * longitudinal);
            float above = smoothstep(-0.05, 0.04, q.y);
            float displacement = wake * mix(-0.068, 0.13, above) * exp(-abs(q.y) * 4.5);
            float phase = (q.y - displacement) * count;
            float d = abs(fract(phase + 0.5) - 0.5) / count;
            vec4 line = mix(color2, color3, 0.34 + 0.46 * above);
            result = mix(color1, line, coverage(d, 0.0035));
            float foil = max(abs(q.y - camber) - thickness, max(-0.32 - q.x, q.x - 0.32));
            result = mix(result, color3, coverage(foil, 0.0));
            result = mix(result, color4, coverage(abs(foil), 0.004));
        """.trimIndent()

        PatternTextureType.MolecularModel -> """
            vec2 row = vec2(p.x + mod(floor(p.y + 0.5), 2.0) * 0.46, p.y);
            vec2 cell = floor(row + 0.5), q = fract(row + 0.5) - 0.5;
            float angle = (hash(cell) - 0.5) * variability;
            q = mat2(cos(angle), -sin(angle), sin(angle), cos(angle)) * q;
            result = color1;
            float bonds = 10.0, atoms = 10.0;
            for (int i = 0; i < 6; i++) {
                float a = float(i) * TAU / 6.0;
                vec2 v = vec2(cos(a), sin(a)), next = vec2(cos(a + TAU / 6.0), sin(a + TAU / 6.0));
                vec2 node = v * 0.196, neighbour = next * 0.196;
                bonds = min(bonds, segment(q, node, neighbour));
                if (mod(float(i), 2.0) < 0.5) bonds = min(bonds, segment(q, node * 0.79, neighbour * 0.79));
                vec2 end = v * (0.323 + 0.034 * mod(float(i), 3.0));
                bonds = min(bonds, segment(q, node, end));
                float endRadius = mod(float(i), 2.0) < 1.0 ? 0.046 : 0.031;
                atoms = min(atoms, length(q - end) - endRadius);
            }
            result = mix(result, color4, coverage(bonds, 0.008));
            result = mix(result, color2, coverage(atoms, 0.009));
            result = mix(result, color3, coverage(atoms, -0.001));
        """.trimIndent()

        PatternTextureType.MarshHeron -> """
            vec2 q = p;
            float waterline = 0.16;
            result = mix(color1, color2, 0.18 * noise(q * 4.0));
            result = mix(result, color4, coverage(length(q - vec2(-0.22, -0.22)), 0.132));
            float water = smoothstep(waterline - pixelWidth, waterline + pixelWidth, q.y);
            float ripple = sin(q.y * 180.0 + 0.70 * sin(q.x * 21.0) + noise(q * 18.0));
            result = mix(result, mix(color2, color1, 0.10 + 0.19 * smoothstep(0.66, 0.94, ripple)), water);
            for (int i = 0; i < 34; i++) {
                float n = float(i);
                float x = (hash(vec2(n, 2.0)) - 0.5) * 1.22;
                float height = 0.038 + 0.13 * hash(vec2(n, 9.0));
                float foot = waterline + 0.022 * hash(vec2(n, 7.0));
                vec2 base = vec2(x, foot);
                vec2 tip = base + vec2((hash(vec2(n, 4.0)) - 0.42) * variability * 0.10, -height);
                result = mix(result, color2, coverage(segment(q, base, tip), 0.0021));
            }
            vec2 hip = vec2(0.12, 0.065);
            float legs =
                    min(segment(q, hip, vec2(0.135, 0.265)), segment(q, vec2(0.135, 0.265), vec2(0.145, 0.34)));
            legs = min(legs, min(segment(q, hip - vec2(0.045, 0.0), vec2(0.028, 0.195)),
                                 segment(q, vec2(0.028, 0.195), vec2(-0.043, 0.151))));
            result = mix(result, color3, coverage(legs, 0.0042));
            vec2 v = rotatePoint(q - vec2(0.105, 0.016), -0.29);
            float bird = ellipseDistance(v, vec2(0.16, 0.071));
            bird = min(bird, triangleDistance(q, vec2(0.15, -0.018), vec2(0.32, 0.125), vec2(0.14, 0.066)));
            vec2 a = vec2(-0.004, -0.006);
            for (int i = 1; i <= 16; i++) {
                float t = float(i) / 16.0;
                vec2 b = vec2(-0.004 - 0.100 * sin(t * 4.6) - t * 0.048, -0.006 - 0.235 * t);
                bird = min(bird, segment(q, a, b) - mix(0.029, 0.016, t));
                a = b;
            }
            bird = min(bird, ellipseDistance(q - a + vec2(0.015, 0.0), vec2(0.052, 0.024)));
            result = mix(result, color3, coverage(bird, 0.0));
            float bill =
                    triangleDistance(q, a + vec2(-0.046, -0.009), a + vec2(-0.142, 0.004), a + vec2(-0.044, 0.009));
            result = mix(result, color4, coverage(bill, 0.0));
            result = mix(result, color4, coverage(length(q - a + vec2(0.026, 0.004)), 0.0048));
            float wing = ellipseDistance(rotatePoint(q - vec2(0.105, 0.012), -0.29), vec2(0.103, 0.038));
            result = mix(result, color2, 0.43 * coverage(wing, 0.0));
        """.trimIndent()

        PatternTextureType.SwitchbackPass -> """
            vec2 q = p;
            result = mix(color1, color2, 0.16 * noise(q * 2.3));
            result = mix(result, color2, 0.69 * coverage(length(q - vec2(0.25, -0.30)), 0.085));
            for (int i = 0; i < 4; i++) {
                float n = float(i);
                float ridge = -0.20 + n * 0.102 + 0.085 * sin(5.0 * q.x + 2.3 * n) +
                              0.085 * noise(vec2(6.0 * q.x, 3.8 * n)) + 0.022 * noise(vec2(q.x * 33.0, n));
                vec4 rock = mix(color2, color3, n * 0.31);
                result = mix(result, rock, smoothstep(ridge - pixelWidth, ridge + pixelWidth, q.y));
            }
            float front = 0.02 - 0.35 * q.x + 0.060 * sin(q.x * 9.0);
            vec4 face = mix(color4, color3, 0.16 + 0.25 * noise(q * vec2(4.0, 12.0)));
            result = mix(result, face, smoothstep(front - pixelWidth, front + pixelWidth, q.y));
            vec2 last = vec2(0.30, -0.06);
            float road = 2.0;
            for (int i = 1; i <= 70; i++) {
                float t = float(i) / 70.0;
                float width = 0.0014 + 0.006 * t;
                vec2 here =
                        vec2(0.30 - 0.35 * t + (0.018 + 0.29 * t) * sin(22.0 * t) * variability, -0.06 + 0.61 * t);
                road = min(road, segment(q, last, here) - width);
                last = here;
            }
            result = mix(result, color1, coverage(road, 0.0) * smoothstep(front - 0.012, front + 0.006, q.y));
        """.trimIndent()

        PatternTextureType.CicadaAmber -> """
            vec2 q = p;
            result = mix(color1, color2, 0.07 + 0.14 * noise(q * 5.0));
            vec2 mirror = vec2(abs(q.x), q.y);
            vec2 wing = rotatePoint(mirror - vec2(0.17, 0.012), 0.53);
            wing.x /= shapeAspect;
            float outer = ellipseDistance(wing, vec2(0.115, 0.32));
            vec2 lower = rotatePoint(mirror - vec2(0.111, 0.125), 0.23);
            float inner = ellipseDistance(lower, vec2(0.080, 0.227));
            vec4 amber = mix(color2, color3, 0.26 + 0.45 * noise(wing * 9.0));
            result = mix(result, mix(color2, color3, 0.38), coverage(inner, 0.0));
            result = mix(result, color4, 0.62 * coverage(abs(inner), 0.003));
            result = mix(result, amber, coverage(outer, 0.0));
            float rib = 1.0;
            for (int i = 0; i < 6; i++) {
                float n = float(i) / 5.0;
                vec2 root = vec2(0.004, -0.278);
                vec2 fork = vec2(-0.090 + 0.16 * n, -0.06 - 0.07 * n);
                vec2 joint = vec2(-0.094 + 0.20 * n, 0.103 + 0.035 * n);
                vec2 tip = vec2(-0.113 + 0.182 * n, 0.16 + 0.135 * sin(n * 2.70));
                rib = min(rib, min(segment(wing, root, fork),
                                   min(segment(wing, fork, joint), segment(wing, joint, tip))));
                rib = min(rib, segment(wing, joint, joint + vec2(0.036, 0.057)));
            }
            result = mix(result, color1, 0.8 * coverage(rib, 0.0028) * coverage(outer, -0.003));
            result = mix(result, color4, coverage(abs(outer), 0.0035));
            float abdomen = ellipseDistance(q - vec2(0.0, 0.057), vec2(0.045, 0.18));
            vec4 bands = mix(color2, color3, 0.74 * smoothstep(0.65, 0.90, cos(q.y * 155.0)));
            result = mix(result, bands, coverage(abdomen, 0.0));
            float thorax = ellipseDistance(q - vec2(0.0, -0.098), vec2(0.067, 0.070));
            result = mix(result, color2, coverage(thorax, 0.0));
            float head = ellipseDistance(q - vec2(0.0, -0.17), vec2(0.064, 0.039));
            result = mix(result, color3, coverage(head, 0.0));
            result = mix(result, color1, coverage(length(mirror - vec2(0.052, -0.175)), 0.017));
            result = mix(result, color4, coverage(length(mirror - vec2(0.055, -0.181)), 0.005));
        """.trimIndent()

        PatternTextureType.Passionflower -> """
            vec2 q = p;
            float r = length(q), a = safeAngle(q);
            result = mix(color1, color2, 0.09 * noise(q * 6.0));
            for (int i = 0; i < 10; i++) {
                float n = float(i);
                vec2 v = rotatePoint(q, -n * TAU / 10.0);
                float curve = 0.024 * sin(v.x * 9.0 + n);
                float leaf = ellipseDistance(v - vec2(0.194, curve), vec2(0.218, 0.062 + 0.010 * mod(n, 2.0)));
                vec4 pigment = mix(color2, color3, 0.25 * mod(n, 2.0) + 0.10 * smoothstep(0.05, 0.38, v.x));
                pigment = mix(pigment, color1, 0.23 * coverage(abs(v.y - curve), 0.0035));
                result = mix(result, pigment, coverage(leaf, 0.0));
            }
            float reach = 0.28 + 0.013 * sin(a * 11.0) + 0.011 * sin(a * 17.0);
            float twist = a * count / TAU + 0.44 * sin(22.0 * r + a * 4.0);
            float thread = abs(fract(twist) - 0.5) * TAU * max(r, 0.03) / count;
            float corona = coverage(thread, 0.0034) * smoothstep(0.072, 0.087, r) *
                           (1.0 - smoothstep(reach - 0.008, reach, r));
            vec4 silk = mix(color3, color2, smoothstep(-0.20, 0.24, cos(r * 88.0)));
            result = mix(result, silk, corona);
            result = mix(result, color1, coverage(r, 0.073));
            for (int i = 0; i < 5; i++) {
                vec2 s = rotatePoint(q, float(i) * TAU / 5.0);
                float stem = segment(s, vec2(0.0), vec2(0.094, 0.0));
                result = mix(result, color2, coverage(stem, 0.006));
                result = mix(result, color4,
                             coverage(ellipseDistance(s - vec2(0.091, 0.0), vec2(0.013, 0.028)), 0.0));
            }
            result = mix(result, color3, coverage(r, 0.027));
        """.trimIndent()

        PatternTextureType.IcebergKeel -> """
            vec2 q = p;
            float level = balance - 0.50;
            result = mix(color1, color2, 0.19 * noise(q * 4.0));
            vec4 ocean = mix(color3, color4, smoothstep(level, 0.5, q.y));
            ocean = mix(ocean, color2, 0.14 * noise(q * vec2(5.0, 17.0)));
            result = mix(result, ocean, smoothstep(level - pixelWidth, level + pixelWidth, q.y));
            vec2 v = q - vec2(0.0, level);
            vec2 peak = vec2(-0.12, -0.235), left = vec2(-0.32, 0.0), tip = vec2(0.29, 0.0);
            vec2 shoulder = vec2(0.14, -0.12), crown = vec2(0.01, -0.09);
            float ridge = triangleDistance(v, left, peak, crown);
            result = mix(result, color2, coverage(ridge, 0.0));
            ridge = triangleDistance(v, peak, vec2(-0.078, 0.0), crown);
            result = mix(result, color1, coverage(ridge, 0.0));
            result = mix(result, color2, coverage(triangleDistance(v, crown, shoulder, tip), 0.0));
            result = mix(result, mix(color1, color2, 0.45), coverage(triangleDistance(v, left, crown, tip), 0.0));
            vec2 ledge = vec2(-0.24, 0.15), foot = vec2(-0.08, 0.44), edge = vec2(0.22, 0.23),
                 heart = vec2(0.035, 0.16);
            result = mix(result, mix(color2, color3, 0.3), coverage(triangleDistance(v, left, ledge, heart), 0.0));
            result = mix(result, mix(color3, color2, 0.63), coverage(triangleDistance(v, left, heart, tip), 0.0));
            result = mix(result, color3, coverage(triangleDistance(v, tip, heart, edge), 0.0));
            result = mix(result, mix(color3, color2, 0.27), coverage(triangleDistance(v, ledge, foot, heart), 0.0));
            result = mix(result, mix(color3, color4, 0.46), coverage(triangleDistance(v, foot, edge, heart), 0.0));
            float seam = abs(v.y - 0.0025 * sin(q.x * 55.0));
            result = mix(result, color1, 0.76 * coverage(seam, 0.003));
        """.trimIndent()

        PatternTextureType.SoundingFlukes -> """
            vec2 q = p;
            float horizon = -0.045;
            result = mix(color1, color2, 0.42 * smoothstep(-0.5, horizon, q.y));
            result = mix(result, color2, coverage(length(q - vec2(-0.30, -0.20)), 0.098));
            float waves = sin(q.y * 123.0 + 1.4 * sin(12.0 * q.x) + noise(q * vec2(9.0, 29.0)) * 3.0);
            vec4 sea = mix(color3, color4, 0.05 + 0.50 * smoothstep(horizon, 0.5, q.y));
            sea = mix(sea, color1, (0.08 + 0.26 * smoothstep(-0.75, 0.5, -q.x)) * smoothstep(0.64, 0.99, waves));
            result = mix(result, sea, smoothstep(horizon - pixelWidth, horizon + pixelWidth, q.y));
            vec2 tail = rotatePoint(q - vec2(0.06, 0.035), -0.11 + 0.11 * sin(time));
            float x = abs(tail.x);
            float t = clamp(x / 0.33, 0.0, 1.0);
            float upper = -0.01 - 0.07 * sin(3.14159 * t) - 0.125 * t * t;
            float lower = 0.042 * (1.0 - t) - 0.125 * t * t + 0.012 * sin(t * 22.0) * sin(3.14159 * t);
            float wing = max(max(upper - tail.y, tail.y - lower), x - 0.33);
            float gap = 0.040 * (1.0 - smoothstep(0.0, 0.026, x));
            wing = max(wing, upper + gap - tail.y);
            float stalk = max(x - (0.022 + 0.027 * (1.0 - smoothstep(0.0, 0.12, tail.y))),
                              max(-tail.y + 0.015, tail.y - 0.145));
            float fluke = min(wing, stalk);
            vec4 skin = mix(color4, color3, 0.19 + 0.16 * noise(tail * 40.0));
            result = mix(result, skin, coverage(fluke, 0.0));
            result = mix(result, color1, 0.47 * coverage(abs(tail.y - lower + 0.004), 0.003) * coverage(wing, 0.0));
            vec2 ring = (q - vec2(0.077, 0.18)) / vec2(1.0, 0.14);
            float wake = abs(fract(length(ring) * 17.0 - time * 0.5) - 0.5) / 17.0;
            result = mix(result, color1, coverage(wake, 0.005) * (1.0 - smoothstep(0.11, 0.29, length(ring))));
        """.trimIndent()

        PatternTextureType.SamaraFlight -> """
            vec2 tile = p + vec2(0.5 * floor(p.y), 0.0);
            vec2 id = floor(tile);
            vec2 q = fract(tile) - 0.5;
            q = rotatePoint(q, (hash(id) - 0.5) * 3.1 * variability + 0.48);
            result = mix(color1, color3, 0.10 * noise(p * 2.0));
            for (int i = 0; i < 2; i++) {
                float side = float(i) * 2.0 - 1.0;
                vec2 v = rotatePoint(vec2(q.x * side, q.y) - vec2(0.035, 0.075), -0.56);
                float t = clamp(v.x / 0.34, 0.0, 1.0);
                float spine = -0.070 * sin(t * 3.14159) - 0.11 * t * t;
                float width = 0.092 * pow(max(0.0, sin(t * 3.14159)), 0.63) * (0.48 + 0.52 * t);
                float edge = max(abs(v.y - spine) - width, max(-v.x, v.x - 0.34));
                vec4 wing = mix(color2, color3, 0.2 + 0.75 * t);
                float along = (v.y - spine) / max(0.018, width);
                float vein = abs(fract(along * 2.5 - 1.3 * t) - 0.5) * width / 2.5;
                wing = mix(wing, color4, 0.75 * coverage(vein, 0.0025));
                result = mix(result, wing, coverage(edge, 0.0));
                result = mix(result, color4, 0.65 * coverage(abs(edge), 0.003));
                float nut = ellipseDistance(v - vec2(0.015, 0.0), vec2(0.047, 0.033));
                result = mix(result, color2, coverage(nut, 0.0));
            }
        """.trimIndent()

        PatternTextureType.OctopusArm -> """
            vec2 q = p;
            result = mix(color1, color2, 0.12 + 0.22 * noise(p * 4.0));
            vec2 a = vec2(-0.59, 0.26);
            float skin = 2.0, underside = 2.0, rim = 2.0, well = 2.0, glint = 2.0;
            for (int i = 1; i <= 80; i++) {
                float t = float(i) / 80.0;
                vec2 b;
                if (t < 0.45) {
                    float u = t / 0.45;
                    b = mix(vec2(-0.59, 0.26), vec2(0.12, 0.205), u);
                    b.y += 0.10 * sin(TAU * u) * sin(3.14159 * u) * variability;
                } else {
                    float u = (t - 0.45) / 0.55;
                    float angle = 1.5708 - u * (6.45 + 1.75 * variability);
                    float radius = mix(0.255, 0.023, u);
                    b = vec2(0.12, -0.05) + radius * vec2(cos(angle), sin(angle));
                }
                float width = 0.090 * (1.0 - t) + 0.008;
                vec2 tangent = b - a;
                vec2 normal = vec2(-tangent.y, tangent.x) / max(length(tangent), 0.0001);
                skin = min(skin, segment(q, a, b) - width);
                underside = min(underside,
                                segment(q, a + normal * width * 0.34, b + normal * width * 0.34) - width * 0.60);
                glint = min(glint,
                            segment(q, a - normal * width * 0.64, b - normal * width * 0.64) - width * 0.075);
                if (mod(float(i), 3.0) < 0.5) {
                    vec2 cup = b + normal * width * (0.34 + 0.25 * cos(float(i)));
                    float radius = width * 0.34;
                    vec2 v = q - cup;
                    float d = length(v);
                    rim = min(rim, d - radius);
                    well = min(well, length(v - normal * radius * 0.14) - radius * 0.52);
                }
                a = b;
            }
            vec4 flesh = mix(color3, color1, 0.16 + 0.14 * noise(q * 40.0));
            result = mix(result, flesh, coverage(skin, 0.0));
            result = mix(result, color3, 0.78 * coverage(glint, 0.0) * coverage(skin, 0.0));
            result = mix(result, mix(color3, color4, 0.50), coverage(underside, 0.0) * coverage(skin, 0.0));
            result = mix(result, color4, coverage(rim, 0.0));
            result = mix(result, mix(color1, color3, 0.48), coverage(well, 0.0));
        """.trimIndent()

        PatternTextureType.WardianCase -> """
            vec2 q = p;
            result = mix(color1, color2, 0.12 + 0.18 * noise(q * 3.0));
            float dome = max(length(vec2(q.x, min(q.y + 0.025, 0.0))) - 0.29, q.y - 0.28);
            vec4 glass = mix(color1, color2, 0.25 + 0.32 * smoothstep(-0.30, 0.32, -q.x));
            result = mix(result, glass, coverage(dome, 0.0));
            float earth = 0.16 + 0.02 * sin(13.0 * q.x) + 0.018 * noise(q * vec2(20.0, 0.0));
            if (q.y > earth) {
                vec4 bed = mix(color3, color1, 0.49 + 0.18 * noise(q * 65.0));
                bed = mix(bed, color2, 0.58 * coverage(abs(q.y - earth - 0.036), 0.009));
                result = mix(result, bed, coverage(dome, -0.005));
            }
            for (int i = 0; i < 6; i++) {
                if (float(i) >= layers) break;
                float n = float(i);
                float x = 0.19 * cos(n * 2.39996);
                vec2 base = vec2(x, 0.178);
                float a = (hash(vec2(n, 2.0)) - 0.5) * 1.04;
                vec2 v = rotatePoint(q - base, a);
                float h = 0.18 + 0.13 * hash(vec2(n, 6.0));
                float frond = segment(v, vec2(0.0), vec2(0.0, -h));
                for (int j = 1; j <= 7; j++) {
                    float u = float(j) / 8.0;
                    vec2 from = vec2(0.0, -u * h);
                    vec2 to = from + vec2(0.060 * sin(u * 3.14159), -0.030);
                    frond = min(frond, segment(vec2(abs(v.x), v.y), from, to));
                }
                result = mix(result, mix(color2, color3, 0.23 + 0.42 * hash(vec2(n, 1.0))),
                             coverage(frond, 0.0044) * coverage(dome, -0.009));
            }
            result = mix(result, color4, 0.86 * coverage(abs(dome), 0.0036));
            float glint = abs(length(vec2(q.x, min(q.y + 0.025, 0.0))) - 0.266);
            result = mix(result, color4,
                         0.58 * coverage(glint, 0.0047) * smoothstep(0.07, 0.20, -q.x) *
                                 (1.0 - smoothstep(0.05, 0.14, q.y)));
            float plinth = max(abs(q.x) - 0.32, abs(q.y - 0.302) - 0.024);
            result = mix(result, color3, coverage(plinth, 0.0));
            result = mix(result, color4, coverage(segment(q, vec2(-0.30, 0.289), vec2(0.30, 0.289)), 0.0026));
        """.trimIndent()

        PatternTextureType.AntGalleries -> """
            vec2 q = p;
            float ground = -0.36 + 0.022 * sin(q.x * 9.0) + 0.014 * noise(vec2(q.x * 18.0, 0.0));
            float layer = q.y + 0.032 * sin(q.x * 9.0) + 0.027 * noise(q * 6.0);
            float stratum = 0.5 + 0.5 * sin(layer * 47.0);
            result = mix(color2, color3, 0.11 + 0.21 * stratum + 0.27 * noise(p * 73.0));
            result = mix(result, color3, 1.0 - smoothstep(ground - pixelWidth, ground + pixelWidth, q.y));
            float hollow = 3.0, insects = 3.0;
            vec2 prev = vec2(-0.19, ground);
            for (int i = 0; i < 8; i++) {
                float n = float(i);
                vec2 c = vec2(-0.15 + 0.085 * sin(n * 2.31) - 0.04 * cos(n), -0.28 + n * 0.106);
                float side = mod(n, 2.0) * 2.0 - 1.0;
                float width = 0.21 + 0.12 * hash(vec2(n, 3.0));
                vec2 pocket = c + vec2(side * width, -0.02 + 0.060 * sin(n * 1.9));
                if (side < 0.0) pocket.x += 0.08;
                vec2 arm = c;
                for (int j = 1; j <= 6; j++) {
                    float u = float(j) / 6.0;
                    vec2 v = mix(c, pocket, u);
                    v.y += 0.033 * sin(3.14159 * u) * cos(2.8 * n + u) * variability;
                    hollow = min(hollow, segment(q, arm, v) - 0.009);
                    arm = v;
                }
                hollow = min(hollow, segment(q, prev, c) - 0.012);
                vec2 v = rotatePoint(q - pocket, 0.36 * sin(n * 2.4));
                float chamber = ellipseDistance(
                        v, vec2(0.058 + 0.033 * hash(vec2(n, 2.0)), 0.030 + 0.014 * hash(vec2(n, 7.0))));
                hollow = min(hollow, chamber + 0.004 * sin(v.x * 80.0));
                float offset = sin(n * 2.23) * 0.035;
                vec2 ant = v - vec2(offset, 0.006);
                float bug = min(ellipseDistance(ant + vec2(0.014, 0.0), vec2(0.010, 0.006)),
                                min(length(ant) - 0.0045, length(ant - vec2(0.010, 0.0)) - 0.0055));
                for (int k = 0; k < 3; k++) {
                    float leg = float(k) - 1.0;
                    bug = min(bug, segment(vec2(ant.x, abs(ant.y)), vec2(leg * 0.004, 0.0025),
                                           vec2(leg * 0.010 - 0.003, 0.012)) -
                                           0.0013);
                }
                insects = min(insects, bug);
                prev = c;
            }
            result = mix(result, color3, coverage(hollow, 0.0055));
            result = mix(result, mix(color4, color1, 0.40 + 0.35 * noise(p * 17.0)), coverage(hollow, 0.0));
            result = mix(result, color3, coverage(insects, 0.0));
        """.trimIndent()

        PatternTextureType.RainShafts -> """
            vec2 q = p;
            float cloud = -0.16 + 0.070 * sin(q.x * 5.0) + 0.085 * noise(vec2(q.x * 6.0, 1.0)) +
                          0.023 * noise(vec2(q.x * 28.0, 0.0));
            vec4 sky = mix(color2, color3, smoothstep(-0.28, 0.29, q.y));
            sky = mix(sky, color4, 0.68 * exp(-abs(q.y - 0.20) * 5.0) * smoothstep(0.01, 0.36, q.x));
            result = sky;
            float falling = q.x + 0.29 * q.y;
            float curtain =
                    0.24 + 0.54 * noise(vec2(falling * 5.7, 1.2)) + 0.15 * noise(vec2(falling * 30.0, q.y * 1.2));
            curtain = smoothstep(0.43, 0.73, curtain) * intensity;
            float streak = 0.65 + 0.35 * noise(vec2(falling * 148.0, q.y * 2.6));
            result = mix(result, color2,
                         curtain* streak* smoothstep(-0.20, 0.08, q.y) * (1.0 - smoothstep(0.26, 0.43, q.y)));
            vec4 bank = mix(color1, color2, 0.23 + 0.43 * noise(q * vec2(5.0, 12.0)));
            result = mix(result, bank, 1.0 - smoothstep(cloud - 0.020, cloud + 0.02, q.y));
            float far = 0.24 + 0.028 * sin(q.x * 12.0) + 0.023 * noise(vec2(q.x * 17.0, 2.0));
            float near = 0.35 + 0.074 * sin(q.x * 4.4 - 0.4) + 0.022 * noise(vec2(q.x * 15.0, 3.0));
            result = mix(result, mix(color2, color1, 0.39), smoothstep(far - pixelWidth, far + pixelWidth, q.y));
            vec4 moor = mix(color1, color2, 0.12 + 0.16 * noise(q * vec2(7.0, 24.0)));
            result = mix(result, moor, smoothstep(near - pixelWidth, near + pixelWidth, q.y));
        """.trimIndent()

        PatternTextureType.PitcherPlants -> """
            vec2 cell = vec2(p.x + floor(p.y) * 0.5, p.y);
            vec2 q = fract(cell) - 0.5;
            float tilt = (hash(floor(cell)) - 0.5) * 0.36;
            vec2 v = rotatePoint(q, tilt);
            result = mix(color1, color2, 0.13 + 0.20 * noise(p * 3.0));
            float strand = segment(v, vec2(-0.04, -0.40), vec2(-0.20, -0.23));
            strand = min(strand, segment(v, vec2(-0.20, -0.23), vec2(-0.19, 0.12)));
            strand = min(strand, segment(v, vec2(-0.19, 0.12), vec2(-0.070, 0.27)));
            result = mix(result, color2, coverage(strand, 0.009));
            float t = clamp((v.y + 0.18) / 0.49, 0.0, 1.0);
            float radius = (0.13 - 0.052 * sin(t * 4.5)) * sqrt(max(0.0, 1.0 - t * t)) * shapeAspect;
            float edge = max(abs(v.x - 0.040 * sin(t * 4.0)) - radius, max(-0.18 - v.y, v.y - 0.31));
            vec4 skin = mix(color2, color3, 0.24 + 0.73 * t);
            skin = mix(skin, color4, 0.60 * smoothstep(0.58, 0.71, noise(v* vec2(38.0, 25.0))));
            result = mix(result, skin, coverage(edge, 0.0));
            vec2 mouth = v - vec2(0.0, -0.178);
            float lip = ellipseDistance(mouth, vec2(0.144 * shapeAspect, 0.048));
            result = mix(result, color4, coverage(lip, 0.0));
            result = mix(
                    result, color1,
                    coverage(ellipseDistance(mouth - vec2(0.0, -0.002), vec2(0.115 * shapeAspect, 0.029)), 0.0));
            float lidStem = segment(v, vec2(0.09, -0.205), vec2(0.135, -0.312));
            result = mix(result, color2, coverage(lidStem, 0.011));
            float lid = ellipseDistance(rotatePoint(v - vec2(0.074, -0.316), 0.27), vec2(0.099, 0.031));
            result = mix(result, color3, coverage(lid, 0.0));
        """.trimIndent()

        PatternTextureType.PoleFoldShibori -> """
            vec2 q = rotatePoint(p, -0.47);
            float bend = 0.017 * sin(q.y * 12.0) + 0.014 * sin(q.y * 27.0 + q.x * 4.0);
            float wick = noise(q * vec2(28.0, 4.0)) * 0.058 + noise(q * 94.0) * 0.014;
            float pinched = q.x + bend + wick + variability * 0.08 * sin(q.y * 5.0 + q.x * 2.3);
            float rib = abs(fract(pinched * 10.0 + 0.16 * sin(q.y * 31.0)) - 0.5);
            float wash = noise(q * vec2(4.0, 10.0));
            float resist = smoothstep(0.17, 0.4, rib + 0.09 * sin(q.y * 35.0 + q.x * 13.0));
            float bound = sin(q.y * 42.0 + q.x * 9.0 + 1.5 * wash);
            vec4 dye = mix(color1, color2, smoothstep(0.16, 0.8, wash));
            vec4 pale = mix(color3, color4, clamp(0.56 + 0.56 * bound + 0.23 * noise(q * 80.0), 0.0, 1.0));
            result = mix(dye, pale, resist * (0.57 + 0.4 * smoothstep(-0.4, 0.5, bound)));
        """.trimIndent()

        PatternTextureType.PeacockMarbling -> """
            vec2 q = p;
            q.x += 0.047 * sin(q.y * 5.0) + 0.028 * sin(q.y * 11.0);
            float drag = 0.05 * sin(q.x * TAU * 2.0 + q.y * 1.8);
            for (int i = -1; i <= 1; i++) {
                float pin = floor(q.x) + float(i) + 0.5;
                float dx = (q.x - pin) / radius;
                drag += 0.66 * exp(-dx * dx);
            }
            float phase = q.y * 3.1 - drag * 3.6;
            phase += 0.076 * sin((q.x + phase * 0.09) * 40.0) + 0.026 * sin(q.x * 66.0 + phase);
            float stripe = fract(phase);
            result = mix(color1, color2, smoothstep(0.08, 0.19, stripe));
            result = mix(result, color3, smoothstep(0.52, 0.57, stripe));
            result = mix(result, color4, smoothstep(0.63, 0.69, stripe));
            result = mix(result, color1, smoothstep(0.91, 0.98, stripe));
            float vein = abs(fract(phase * 2.0 + 0.09) - 0.5);
            result = mix(result, color3, 0.72 * coverage(vein * 0.14, 0.004));
        """.trimIndent()

        PatternTextureType.LogCabinQuilt -> """
            vec2 patch = floor(p);
            vec2 q = fract(p) - 0.5;
            if (mod(patch.x, 2.0) < 1.0) q.x = -q.x;
            if (mod(patch.y, 2.0) < 1.0) q.y = -q.y;
            vec2 local = q + vec2(0.051, -0.037);
            float ring = floor(max(abs(local.x), abs(local.y)) * 9.0);
            float angle = safeAngle(local);
            float side = floor((angle + 0.125 * TAU + TAU) / (TAU * 0.25));
            float warm = step(0.0, local.x - local.y);
            float lighter = mod(ring + side, 2.0);
            vec4 cloth = warm > 0.5 ? mix(color3, color4, lighter * 0.85) : mix(color1, color2, lighter * 0.88);
            float axis = abs(local.x) > abs(local.y) ? local.y : local.x;
            float weave = 0.5 + 0.5 * sin((axis * 64.0 + 0.2 * sin(axis * 28.0)) * TAU);
            result = mix(cloth, color4, 0.08 * weave);
            float seam = abs(fract(max(abs(local.x), abs(local.y)) * 9.0 + 0.5) - 0.5) / 9.0;
            result = mix(result, color1, 0.6 * coverage(seam, 0.004));
            float stitch =
                    coverage(abs(seam - 0.012), thickness) * coverage(abs(fract(axis * 28.0) - 0.5) / 28.0, 0.010);
            result = mix(result, color4, stitch * 0.8);
            result = mix(result, color3, coverage(max(abs(local.x), abs(local.y)), 0.082));
        """.trimIndent()

        PatternTextureType.CoiledRagRug -> """
            vec2 q = vec2(p.x * 0.96, p.y * 1.08);
            float radius = length(q), angle = safeAngle(q);
            float turn = radius * 18.0 - angle / TAU;
            float coil = fract(turn), ridge = max(0.0, 1.0 - pow(2.0 * abs(coil - 0.5), 2.0));
            float braid = sin(angle * floor(10.0 + radius * 122.0) + sin(TAU * coil) * 3.1);
            float swath = fract((floor(turn) + angle / TAU) * 0.178 + 0.12 * sin(angle * 2.0));
            vec4 yarn = swath < 0.25 ? color2 : (swath < 0.53 ? color3 : (swath < 0.78 ? color1 : color4));
            vec4 fleck = mix(yarn, color4, 0.52);
            result = mix(yarn, fleck, smoothstep(0.14, 0.81, braid) * 0.75);
            result = mix(result, color1, shadows * (1.0 - sqrt(ridge)) * 0.80);
            float fiber = noise(vec2(radius * 225.0, braid * 2.7));
            result = mix(result, color4, 0.11 * fiber + 0.10 * smoothstep(0.87, 0.99, ridge));
        """.trimIndent()

        PatternTextureType.ButtonTufting -> """
            vec2 grid = vec2(p.x + p.y * 0.6, p.x - p.y * 0.6);
            vec2 q = fract(grid) - 0.5;
            vec2 wall = 0.5 - abs(q);
            float fold = min(wall.x, wall.y);
            float cushion = pow(max(sin(wall.x * TAU * 0.5) * sin(wall.y * TAU * 0.5), 0.0), 0.43);
            float lean = 0.5 + 0.5 * sin((q.x + q.y) * 2.5);
            vec4 velvet = mix(color1, color2, smoothstep(0.01, 0.48, cushion));
            velvet = mix(velvet, color3, 0.54 * cushion * lean);
            float pinch = exp(-fold * 26.0) * (0.5 + 0.5 * cos((wall.x + wall.y) * 57.0));
            result = mix(velvet, color1, shadows * (0.5 * pinch + 0.20 * (1.0 - cushion)));
            float nap = noise(p * vec2(49.0, 95.0));
            result = mix(result, color3, 0.065 * nap);
            vec2 stud = vec2(wall.x + wall.y, wall.x - wall.y) * vec2(0.56, 0.82);
            float r = length(stud);
            result = mix(result, color1, coverage(r, 0.068));
            result = mix(result, color4, coverage(r, 0.045));
            result = mix(result, color2, 0.38 * coverage(r, 0.022));
        """.trimIndent()

        PatternTextureType.TornPosterWall -> """
            vec2 u = rotatePoint(p, 0.16);
            float tear = 0.075 * sin(u.x * 9.0) + 0.052 * noise(vec2(u.x * 13.0, 4.3));
            tear += variability * (0.054 * noise(vec2(u.x * 45.0, 9.0)) + 0.022 * noise(vec2(u.x * 161.0, 2.0)));
            float y = u.y + tear;
            float bands = mod(floor(y * 4.0), 4.0);
            vec4 ground = bands < 1.0 ? color1 : (bands < 2.0 ? color2 : (bands < 3.0 ? color3 : color4));
            float printShape;
            if (bands < 1.0) {
                printShape = coverage(abs(fract(u.x * 4.5) - 0.5) / 4.5, 0.044);
            } else if (bands < 2.0) {
                vec2 disk = fract(vec2(u.x * 2.7, u.y * 2.7)) - 0.5;
                printShape = coverage(abs(length(disk) - 0.35) / 2.7, 0.024);
            } else {
                printShape = coverage(abs(fract((u.x + u.y) * 10.0) - 0.5) / 10.0, 0.013);
            }
            result = mix(ground, bands < 2.0 ? color4 : color1, printShape * 0.82);
            float edge = fract(y * 4.0) / 4.0;
            result = mix(result, color1, 0.68 * coverage(edge, 0.011));
            result = mix(result, color4, coverage(edge, 0.005 + 0.004 * noise(u * 90.0)));
        """.trimIndent()

        PatternTextureType.LatteRosetta -> """
            vec2 q = rotatePoint(p, 0.21);
            float cup = length(q);
            float crema = noise(q * 15.0) * 0.65 + noise(q * 56.0) * 0.35;
            result = mix(color1, color2, 0.28 + crema * 0.71);
            result =
                    mix(result, color3, 0.42 * coverage(abs(cup - 0.437 - 0.008 * sin(safeAngle(q) * 6.0)), 0.013));
            float milk = 0.0;
            for (int i = 0; i < 13; i++) {
                float t = float(i) / 12.0;
                float width = 0.040 + 0.245 * sin(t * 2.65);
                float root = -0.260 + t * 0.475;
                float x = abs(q.x) / width;
                float lobe = root - 0.12 * sin(min(x, 1.0) * 1.48) * (1.0 - 0.54 * t);
                float taper = max(0.0, 1.0 - x * x);
                milk = max(milk, coverage(abs(q.y - lobe), thickness * (0.42 + 0.58 * t) * taper) *
                                         coverage(abs(q.x), width));
            }
            float stem = segment(q, vec2(0.0, -0.335), vec2(0.003, 0.246));
            milk = max(milk, coverage(stem, 0.009));
            result = mix(result, color4, milk);
            result = mix(result, color3, coverage(abs(cup - 0.469), 0.008));
        """.trimIndent()

        PatternTextureType.SalmonMarbling -> """
            vec2 q = rotatePoint(p, -0.27);
            float y = q.y + 0.085 * sin(q.x * 4.0) + 0.022 * sin(q.x * 17.0 + q.y * 2.0);
            float notch = 0.62 * sqrt(q.x * q.x + 0.012);
            float phase = (y - notch) * 7.6 + 0.24 * sin(q.x * 4.0 - y) + 0.12 * sin(q.x * 10.0 + y * 3.0);
            float seam = abs(fract(phase + 0.5) - 0.5);
            float fiber = 0.5 + 0.5 * sin((q.x - 0.12 * sin(y * 6.0)) * 112.0 + 1.8 * noise(q * 15.0));
            float tone = 0.3 + 0.56 * noise(q * 5.0) + 0.14 * fiber;
            result = mix(color1, color2, tone);
            result = mix(result, color3, 0.27 * smoothstep(0.15, 0.49, seam));
            float fat = thickness * (0.72 + 0.50 * noise(q * 18.0));
            result = mix(result, color3, coverage(seam / 7.6, fat * 1.55));
            result = mix(result, color4, coverage(seam / 7.6, fat));
        """.trimIndent()

        PatternTextureType.PomegranateCut -> """
            vec2 q = rotatePoint(p, -0.19) - vec2(0.0, 0.022);
            float angle = safeAngle(q), r = length(q);
            float outline = 0.393 + 0.023 * sin(angle * 5.0) + 0.008 * sin(3.0 * angle + 0.8);
            vec2 cap = q - vec2(0.013, -0.378);
            float calyx = triangleDistance(cap, vec2(-0.080, 0.040), vec2(-0.106, -0.088), vec2(-0.021, -0.040));
            calyx = min(calyx, triangleDistance(cap, vec2(-0.040, 0.036), vec2(0.004, -0.104), vec2(0.044, 0.032)));
            calyx = min(calyx, triangleDistance(cap, vec2(0.018, -0.034), vec2(0.104, -0.083), vec2(0.077, 0.042)));
            result = mix(color1, color2, coverage(calyx, 0.0));
            result = mix(result, color3, coverage(calyx, -0.012));
            result = mix(result, color2, coverage(r, outline + 0.014));
            result = mix(result, color4, coverage(r, outline));
            vec2 position = q * 16.0;
            vec2 base = floor(position), nearestCell = base, local = vec2(0.0);
            float nearest = 5.0, second = 5.0;
            for (int y = -1; y <= 1; y++)
                for (int x = -1; x <= 1; x++) {
                    vec2 cell = base + vec2(float(x), float(y));
                    vec2 v = position - cell - 0.5 - 0.65 * (hash2(cell) - 0.5);
                    float d = dot(v, v);
                    if (d < nearest) {
                        second = nearest;
                        nearest = d;
                        nearestCell = cell;
                        local = v;
                    } else {
                        second = min(second, d);
                    }
                }
            float curled = angle + 0.12 * sin(angle + 6.0 * r) + 0.065 * sin(14.0 * r + angle * 2.0);
            float pith = abs(sin(curled * 3.0 + 0.62 * sin(9.0 * r))) * r / 3.0;
            float seeds = coverage(r, outline - 0.023 - 0.012 * sin(angle * 8.0));
            seeds *= 1.0 - coverage(pith, 0.006 + 0.010 * (1.0 - smoothstep(0.06, 0.3, r)));
            seeds *= 1.0 - coverage(r, 0.047 + 0.015 * sin(4.0 * angle));
            float rim = smoothstep(0.025, 0.30, second - nearest);
            rim *= 1.0 - smoothstep(0.29, 0.62, nearest);
            vec2 glint = (local + vec2(0.13, 0.14) + 0.10 * (hash2(nearestCell + 5.0) - 0.5)) * vec2(1.3, 0.73);
            float glow = exp(-9.0 * dot(glint, glint));
            vec4 aril = mix(color2, color3, (0.3 + 0.50 * hash(nearestCell) + 0.18 * glow) * rim);
            aril = mix(aril, color4, 0.64 * smoothstep(0.80, 1.0, glow));
            result = mix(result, aril, seeds);
        """.trimIndent()

        PatternTextureType.LetterpressOverprint -> """
            vec2 q = fract(p + 0.5) - 0.5;
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
            vec2 q = p + vec2(0.008, 0.018);
            result = color1;
            for (int branch = 0; branch < 3; branch++) {
                vec2 start =
                        branch == 0 ? vec2(-0.48, 0.36) : (branch == 1 ? vec2(0.30, 0.47) : vec2(-0.01, -0.49));
                vec2 bend =
                        branch == 0 ? vec2(-0.24, -0.02) : (branch == 1 ? vec2(-0.06, 0.29) : vec2(0.39, -0.17));
                vec2 end = branch == 0 ? vec2(-0.032, -0.338)
                                       : (branch == 1 ? vec2(0.321, -0.068) : vec2(0.364, 0.002));
                vec2 last = start;
                float edge = 2.0;
                for (int j = 1; j <= 13; j++) {
                    float t = float(j) / 13.0;
                    vec2 nextPoint = mix(mix(start, bend, t), mix(bend, end, t), t);
                    edge = min(edge, segment(q, last, nextPoint));
                    last = nextPoint;
                }
                vec4 paper = branch == 0 ? color3 : (branch == 1 ? color4 : color2);
                result = mix(result, mix(color1, paper, 0.4), coverage(edge, thickness * 1.65));
                result = mix(result, paper, coverage(edge, thickness * 0.74));
            }
            for (int i = 0; i < 5; i++) {
                float k = float(i);
                vec2 center = i == 0 ? vec2(-0.17, -0.094)
                                     : (i == 1 ? vec2(0.217, 0.165)
                                               : (i == 2 ? vec2(0.205, -0.294)
                                                         : (i == 3 ? vec2(-0.225, 0.298) : vec2(0.405, -0.05))));
                vec2 v = q - center;
                float outer = i == 0 ? 0.312 : (i == 1 ? 0.222 : (i == 3 ? 0.131 : 0.114));
                float angle = safeAngle(v) / TAU + 0.5;
                if (i == 1 || i == 3) angle = 1.0 - angle;
                float r = length(v);
                float turn = r / outer * 4.1 - angle + 0.29 * k;
                float strip = abs(fract(turn + 0.5) - 0.5) * outer / 4.1;
                float margin = outer - 0.026 + 0.026 * sin(angle * TAU + k);
                result = mix(result, color1, coverage(r, margin - thickness));
                float body = coverage(r, margin) * smoothstep(0.004, 0.012, r);
                vec4 paper = i == 0 || i == 4 ? color3 : (i == 1 ? color4 : color2);
                float relief = 0.24 + 0.65 * smoothstep(-0.7, 0.9, cos(TAU * angle - 0.67));
                result = mix(result, mix(color1, paper, 0.35 + 0.3 * relief),
                             coverage(strip, thickness * 1.75) * body);
                result = mix(result, paper, coverage(strip, thickness * 0.76) * body);
                float lip = coverage(abs(strip - 0.0025), 0.0018);
                result = mix(result, color2, 0.41 * lip * relief * body);
            }
        """.trimIndent()

        PatternTextureType.GoldLeafTransfer -> """
            vec2 tile = floor(p + vec2(0.16, 0.18)), q = fract(p + vec2(0.16, 0.18));
            vec2 uv = p + 0.07 * vec2(noise(p * 6.0), noise(p * 6.0 + 13.0));
            float brush = noise(uv * vec2(3.0, 19.0));
            float mottling = 0.48 * noise(uv * 3.6) + 0.30 * noise(uv * 9.0) + 0.15 * noise(uv * 25.0) +
                             0.07 * noise(uv * 72.0);
            float seam = min(min(q.x, 1.0 - q.x), min(q.y, 1.0 - q.y));
            float worn = mottling - variability * 0.35 - 0.09 * exp(-seam * 70.0);
            float leaf = smoothstep(0.233, 0.256, worn);
            vec4 bole = mix(color1, color2, brush * 0.82);
            float crinkle = abs(fract(noise(uv * 3.0) * 6.0 + 0.2 * noise(uv * 15.0)) - 0.5);
            float light = 0.20 + 0.45 * brush + 0.18 * hash(tile) + 0.15 * sin((p.x - p.y * 0.7) * 11.0);
            vec4 gold = mix(color3, color4, clamp(light + 0.32 * smoothstep(0.3, 0.49, crinkle), 0.0, 1.0));
            gold = mix(gold, color2, 0.24 * coverage(seam, 0.008));
            result = mix(bole, gold, leaf);
        """.trimIndent()

        PatternTextureType.CaneWebbing -> """
            vec2 q = p + 0.009 * vec2(sin(p.y * TAU), sin(p.x* TAU));
            result = color1;
            for (int pass = 0; pass < 4; pass++) {
                float axis, grain;
                if (pass == 0) {
                    axis = q.x + q.y;
                    grain = q.x - q.y;
                } else if (pass == 1) {
                    axis = q.x - q.y;
                    grain = q.x + q.y;
                } else if (pass == 2) {
                    axis = q.x;
                    grain = q.y;
                } else {
                    axis = q.y;
                    grain = q.x;
                }
                float d = abs(fract(axis + 0.5) - 0.5);
                float transverse = pass < 2 ? d * 0.7071 : abs(d - 0.117);
                float width = thickness * (pass < 2 ? 1.18 : 0.68);
                float broadside = sqrt(max(0.0, 1.0 - transverse * transverse / (width * width)));
                float rough = noise(vec2(grain * 6.0, axis * 55.0));
                float over = 0.5 + 0.5 * cos(grain * 3.14159 + float(pass));
                vec4 cane = mix(color2, color3, 0.25 + 0.47 * broadside + 0.21 * rough);
                cane = mix(cane, color4, 0.40 * broadside * over);
                result = mix(result, mix(color1, cane, 0.32), coverage(transverse, width + 0.015));
                result = mix(result, cane, coverage(transverse, width));
            }
        """.trimIndent()

        PatternTextureType.PleatedHandFan -> """
            vec2 v = p - vec2(-0.018, 0.351);
            float r = length(v), angle = atan(v.x, -v.y);
            float aperture = 1.27;
            float wedge = fract((angle / (2.0 * aperture) + 0.5) * count);
            float face = 1.0 - abs(2.0 * wedge - 1.0);
            float within = coverage(abs(angle) * max(r, 0.01), aperture* max(r, 0.01));
            float tip = 0.641 + 0.019 * face;
            result = color1;
            float stick = coverage(abs(wedge - 0.5) * r * 2.0 * aperture / count, 0.0055);
            result = mix(result, color3, stick* within* coverage(r, 0.592));
            vec4 paper = mix(color2, color3, 0.15 + 0.33 * face);
            float sea = r - 0.50 - 0.058 * sin(angle * 2.8 + 0.7) - 0.018 * sin(angle * 8.0 + 1.0);
            paper = mix(paper, color1, 0.78 * (1.0 - smoothstep(-0.004, 0.004, sea)));
            paper = mix(paper, color4, coverage(abs(sea - 0.014), 0.007));
            float sun = length(v - vec2(0.176, -0.506));
            paper = mix(paper, color4, coverage(sun, 0.066));
            paper = mix(paper, color1, 0.30 * (1.0 - face));
            float leaf = within * coverage(r, tip) * (1.0 - coverage(r, 0.259));
            result = mix(result, paper, leaf);
            result = mix(result, color3, within* coverage(abs(r - tip + 0.016), 0.005));
            result = mix(result, color4, coverage(r, 0.024));
        """.trimIndent()

        PatternTextureType.TailorTape -> """
            result = mix(color1, color4, 0.12 * noise(p * 25.0));
            for (int i = 0; i < 3; i++) {
                float lane = float(i);
                vec2 q = rotatePoint(p, i == 0 ? -0.66 : (i == 1 ? 0.22 : 0.91));
                float wiggle = 0.07 + 0.12 * variability;
                float middle = (lane - 1.0) * 0.14 + wiggle * sin(q.x * (4.0 + lane) + lane * 1.8);
                float slope = wiggle * (4.0 + lane) * cos(q.x * (4.0 + lane) + lane * 1.8);
                float side = (q.y - middle) / sqrt(1.0 + slope * slope);
                float s = q.x + 0.055 * sin(q.x * 5.0 + lane) + lane * 0.031;
                float index = floor(s * 59.0 + 0.5);
                float tick = abs(fract(s * 59.0 + 0.5) - 0.5) / 59.0;
                float major = 1.0 - step(0.45, mod(index, 5.0));
                float mid = 1.0 - step(0.45, mod(index, 2.0));
                float height = 0.018 + 0.014 * mid + 0.025 * major;
                vec4 tape = i == 1 ? color3 : color2;
                vec4 band = mix(tape, color4, 0.14 * (0.4 + 0.6 * sin(q.x * 10.0 + lane)));
                float edge = 0.074 - abs(side);
                band = mix(band, color4, coverage(tick, 0.0021) * coverage(edge, height));
                band = mix(band, color4, coverage(abs(edge - 0.005), 0.0008));
                result = mix(result, color4, 0.63 * coverage(abs(side - 0.006), 0.079));
                result = mix(result, band, coverage(abs(side), 0.074));
            }
        """.trimIndent()

        PatternTextureType.PalmetteFrieze -> """
            vec2 q = fract(p + vec2(0.5, 0.5)) - 0.5;
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
            vec2 q = fract(p + 0.5) - 0.5;
            float y = q.y + 0.5;
            float perspective = 0.24 + 0.86 * y;
            float x = q.x / perspective + 0.16;
            float branch = 0.71 * pow(max(0.0, y - 0.12), 1.72) + offset * y;
            float surface = 0.33 * noise(p * 43.0) + 0.17 * noise(p * 111.0);
            result = mix(color1, color2, surface);
            float ties = abs(fract(8.0 * log(0.19 + y)) - 0.5);
            float bed = min(abs(x), abs(x - branch));
            float rail = min(abs(abs(x) - 0.112), abs(abs(x - branch) - 0.112));
            result = mix(result, color2,
                         coverage(bed* perspective, 0.185 * perspective) * coverage(ties * 0.07, 0.012));
            result = mix(result, color1, coverage(rail* perspective, 0.014 * perspective));
            result = mix(result, color3, coverage(rail* perspective, 0.006 * perspective));
            float lever = segment(q, vec2(-0.342, 0.24), vec2(-0.31, 0.12 - offset * 0.1));
            result = mix(result, color3, coverage(lever, 0.007));
            result = mix(result, color4, coverage(length(q - vec2(-0.31, 0.12 - offset * 0.1)), 0.024));
        """.trimIndent()

        PatternTextureType.SuspensionSpan -> """
            vec2 q = fract(p + 0.5) - 0.5;
            float river = 0.245 + 0.010 * sin(q.x * 15.0);
            result = mix(color1, color2, 0.65 * smoothstep(-0.42, 0.28, q.y));
            float hill = 0.095 + 0.085 * sin(q.x * 5.0 + 0.7) + 0.045 * sin(q.x * 12.0);
            result = mix(result, color2, smoothstep(hill - pixelWidth, hill + pixelWidth, q.y));
            if (q.y > river) {
                float glint = sin(q.y * 189.0 + 1.4 * sin(12.0 * q.x + q.y * 16.0));
                result = mix(color2, color3, 0.21 + 0.40 * smoothstep(0.0, 0.95, glint) * noise(q * 22.0));
            }
            float tower = abs(abs(q.x + 0.015) - 0.27);
            float reach = abs(q.x + 0.015) / 0.27;
            float sag = -0.26 + intensity * (1.0 - reach * reach);
            if (reach > 1.0) sag = -0.26 + (reach - 1.0) * 0.47;
            float cables = abs(q.y - sag);
            float hanger = abs(fract((q.x + 0.015) * 34.0 + 0.5) - 0.5) / 34.0;
            float span = max(hanger, max(sag - q.y, q.y - 0.115));
            result = mix(result, color3, max(coverage(cables, 0.004), coverage(span, 0.0023)));
            float legs = max(tower - 0.017, max(-0.29 - q.y, q.y - 0.27));
            result = mix(result, color4, coverage(legs, 0.0));
            float deck = abs(q.y - 0.127);
            result = mix(result, color3, coverage(deck, 0.013));
            result = mix(result, color4, coverage(abs(q.y - 0.110), 0.004));
        """.trimIndent()

        PatternTextureType.RainCrosswalk -> """
            vec2 q = fract(p + 0.5) - 0.5;
            vec2 road = rotatePoint(q, -0.38);
            float wet = 0.38 * noise(road * vec2(3.0, 15.0)) + 0.27 * noise(road * vec2(29.0, 95.0));
            result = mix(color1, color2, wet);
            float stripe = abs(fract((road.y + 0.024 * road.x) * 9.0) - 0.5) / 9.0;
            float border = max(abs(road.x + 0.06) - 0.263, stripe - 0.024);
            result = mix(result, color3, coverage(border, 0.0) * (0.70 + 0.23 * noise(road * 100.0)));
            vec2 drop = q - vec2(-0.105, 0.13);
            float a = safeAngle(drop);
            float scallop = (0.96 + 0.04 * cos(a * 8.0)) * radius;
            float silhouette = length(drop + vec2(-0.022, -0.020)) - radius;
            result = mix(result, color1, coverage(silhouette, 0.0) * 0.65);
            float facets = 0.5 + 0.5 * cos(floor(a * 8.0 / TAU) * TAU / 8.0 - 0.5);
            result = mix(result, mix(color4, color3, facets * 0.30), coverage(length(drop), scallop));
            float ribs = abs(sin(a * 4.0)) * length(drop) / 4.0;
            result = mix(result, color1, coverage(ribs, 0.0015) * coverage(length(drop), scallop) * 0.44);
            result = mix(result, color1, coverage(length(drop), radius * 0.065));
        """.trimIndent()

        PatternTextureType.CofferedRotunda -> """
            vec2 q = fract(p + 0.5) - vec2(0.46, 0.43);
            float r = length(q);
            float a = safeAngle(q) / TAU;
            float radial = log(1.0 + 15.0 * r) * 3.25;
            float ring = floor(radial);
            float sector = a * floor(count + 0.5) + 0.5 * mod(ring, 2.0);
            vec2 tile = fract(vec2(sector, radial));
            vec2 border = min(tile, 1.0 - tile);
            float edge = min(border.x, border.y);
            float lightSide = tile.x + tile.y < 1.0 ? 1.0 : 0.0;
            result = mix(color1, color2, 0.40 + 0.42 * (1.0 - r));
            result = mix(result, mix(color2, color3, lightSide * 0.88), coverage(edge * 0.06, 0.0085));
            result = mix(result, color4, coverage(edge * 0.06, 0.0015));
            float opening = coverage(r, 0.055);
            result = mix(result, color4, opening);
            result = mix(result, color3, coverage(abs(r - 0.062), 0.005));
        """.trimIndent()

        PatternTextureType.StationCanopy -> """
            vec2 q = fract(p + 0.5) - 0.5;
            vec2 focus = vec2(offset, 0.31);
            vec2 shaft = q - focus;
            float depth = max(0.018, -shaft.y);
            float ray = shaft.x / depth;
            result = mix(color1, color2, 0.38 + 0.54 * smoothstep(-0.45, 0.3, q.y));
            float central = 1.0 - smoothstep(0.12, 1.4, abs(ray));
            result = mix(result, color1, 0.45 * central);
            float column = abs(fract(ray * 1.75 + 0.5) - 0.5) * depth / 1.75;
            float level = log(depth + 0.032) * 3.35;
            float hoop = abs(fract(level) - 0.5) * (depth + 0.032) / 3.35;
            float visibility = smoothstep(0.06, 0.29, depth);
            if (shaft.y < -0.012) {
                result = mix(result, color3, visibility * coverage(column, 0.006 * depth + 0.001));
                result = mix(result, color3, visibility * coverage(hoop, 0.009 * depth + 0.0015));
                float junction = abs(fract(ray * 1.75 + fract(level)) - 0.5) * depth / 1.75;
                result = mix(result, color4, visibility * coverage(junction, 0.002 + 0.002 * depth));
            } else {
                float lip = abs(q.y - focus.y - 0.23 * abs(q.x - focus.x));
                result = mix(color3, color4, coverage(lip, 0.012));
                float platform = abs(shaft.x) - 1.7 * max(0.0, shaft.y);
                result = mix(result, color1, coverage(platform, -0.014) * 0.4);
            }
        """.trimIndent()

        PatternTextureType.PendulumCase -> """
            vec2 q = fract(p + 0.5) - 0.5;
            float caseDepth = abs(q.x) + 0.025 * abs(q.y);
            result = mix(color1, color2, 0.15 + 0.22 * noise(q * vec2(130.0, 3.0)));
            result = mix(result, color2, coverage(abs(caseDepth - 0.316), 0.012));
            vec2 clock = q - vec2(0.0, -0.21);
            float dial = length(clock);
            result = mix(result, color2, coverage(dial, 0.177));
            result = mix(result, color3, coverage(dial, 0.156));
            float a = safeAngle(clock);
            float minutes = abs(sin(a * 30.0)) * dial / 30.0;
            result = mix(result, color2, coverage(minutes, 0.0018) * step(0.138, dial) * step(dial, 0.149));
            float marks = abs(sin(a * 6.0)) * dial / 6.0;
            result = mix(result, color1, coverage(marks, 0.0037) * step(0.12, dial) * step(dial, 0.149));
            float hands = min(segment(clock, vec2(0.0), vec2(-0.074, -0.068)),
                              segment(clock, vec2(0.0), vec2(0.100, -0.054)));
            result = mix(result, color4, coverage(hands, 0.0055));
            vec2 hinge = vec2(0.0, -0.018);
            vec2 bob = hinge + 0.328 * vec2(sin(time) * 0.49, cos(time * 0.47));
            vec2 v = q - bob;
            result = mix(result, color2, coverage(segment(q, hinge, bob), 0.005));
            result = mix(result, color3, coverage(length(v), 0.081));
            result = mix(result, color2, coverage(abs(length(v) - 0.065), 0.0035));
            result = mix(result, color4, coverage(length(v + vec2(0.012, 0.005)), 0.017));
        """.trimIndent()

        PatternTextureType.ScrewBookpress -> """
            vec2 q = fract(p + 0.5) - 0.5;
            float grain = noise(q * vec2(28.0, 5.0));
            result = mix(color1, color2, grain * 0.13);
            float frame = max(abs(abs(q.x) - 0.31) - 0.030, abs(q.y - 0.035) - 0.345);
            float crown = max(abs(q.x) - 0.36, abs(q.y + 0.27) - 0.034);
            float foot = max(abs(q.x) - 0.38, abs(q.y - 0.363) - 0.031);
            result = mix(result, color3, coverage(min(frame, min(crown, foot)), 0.0));
            float screw = max(abs(q.x) - 0.025, max(-0.402 - q.y, q.y - offset + 0.011));
            result = mix(result, mix(color2, color3, smoothstep(0.35, 0.7, fract(q.y * 77.0 - q.x * 19.0))),
                         coverage(screw, 0.0));
            float bar = segment(q, vec2(-0.142, -0.406), vec2(0.145, -0.343));
            result = mix(result, color4, coverage(bar, 0.011));
            float platen = max(abs(q.x) - 0.22, abs(q.y - offset) - 0.025);
            result = mix(result, color3, coverage(platen, 0.0));
            float book = max(abs(q.x - 0.012) - 0.225, abs(q.y - 0.258) - 0.067);
            vec4 pages = mix(color1, color2, 0.53 * smoothstep(0.35, 0.65, fract(q.y * 87.0)));
            result = mix(result, pages, coverage(book, 0.0));
            float covers = max(abs(q.x - 0.012) - 0.24, abs(abs(q.y - 0.258) - 0.068) - 0.009);
            result = mix(result, color4, coverage(covers, 0.0));
        """.trimIndent()

        PatternTextureType.TheatreWings -> """
            vec2 q = fract(p + 0.5) - 0.5;
            result = mix(color1, color2, 0.27 * smoothstep(-0.4, 0.45, q.y));
            float beam = abs(q.x - (0.07 + q.y * 0.24));
            float cone = 1.0 - smoothstep(0.27 * (q.y + 0.53) - 0.035, 0.27 * (q.y + 0.53) + 0.035, beam);
            result = mix(result, color4, cone * (0.13 + 0.20 * smoothstep(-0.35, 0.39, q.y)));
            for (int i = 0; i < 3; i++) {
                float layer = float(i);
                float fold = abs(q.x) - (offset + 0.051 * layer + 0.30 * (1.0 - pow(abs(q.y - 0.19) / 0.72, 1.2)));
                float pleat =
                        0.53 + 0.36 * cos((q.x + sign(q.x) * (0.12 * q.y - 0.045 * layer)) * (52.0 + 5.0 * layer));
                vec4 velvet = mix(color2, color3, pleat * (0.54 + 0.16 * layer));
                result = mix(result, velvet, 1.0 - coverage(fold, 0.0));
            }
            float apron = q.y - 0.38 - 0.06 * q.x * q.x;
            result = mix(result, color1, smoothstep(-pixelWidth, pixelWidth, apron));
            float footlight = length(vec2(fract(q.x * 12.0 + 0.5) - 0.5, (q.y - 0.42) * 12.0)) / 12.0;
            result = mix(result, color4, coverage(footlight, 0.008));
        """.trimIndent()

        PatternTextureType.HornGramophone -> """
            vec2 q = fract(p + 0.5) - 0.5;
            vec2 horn = rotatePoint(q - vec2(0.047, -0.14), -0.35);
            vec2 polar = horn / vec2(1.0, 0.77);
            float a = safeAngle(polar), r = length(polar);
            float lip = radius * (0.95 + 0.047 * cos(9.0 * a));
            result = mix(color1, color2, 0.16 * smoothstep(-0.45, 0.39, q.y));
            float tube = min(segment(q, vec2(-0.20, 0.185), vec2(-0.26, 0.09)),
                             segment(q, vec2(-0.26, 0.09), vec2(-0.07, -0.13)));
            result = mix(result, color3, coverage(tube, 0.022));
            float petal = 0.48 + 0.40 * cos(a * 9.0);
            vec4 brass = mix(color2, color3, 0.27 + petal * smoothstep(0.0, radius, r));
            result = mix(result, brass, coverage(r, lip));
            result = mix(result, color4, coverage(abs(r - lip), 0.006));
            float throat = length(polar + vec2(0.055, -0.012));
            result = mix(result, color1, coverage(throat, radius * 0.19));
            float box = max(abs(q.x + 0.035) - 0.27, abs(q.y - 0.275) - 0.07);
            result = mix(result, color2, coverage(box, 0.0));
            float mould = max(abs(q.x + 0.035) - 0.286, abs(abs(q.y - 0.275) - 0.075) - 0.009);
            result = mix(result, color3, coverage(mould, 0.0));
            float platter = ellipseDistance(q - vec2(-0.012, 0.195), vec2(0.19, 0.029));
            result = mix(result, color1, coverage(platter, 0.0));
            result = mix(result, color4,
                         coverage(ellipseDistance(q + vec2(0.012, -0.195), vec2(0.048, 0.009)), 0.0));
        """.trimIndent()

        PatternTextureType.CanalLock -> """
            vec2 q = rotatePoint(fract(p + 0.5) - 0.5, 0.28);
            float x = abs(q.x), y = q.y;
            float wave = sin(y * 81.0 + 1.5 * sin(q.x * 11.0 + 3.0 * y));
            result = mix(color1, color2, 0.2 + 0.27 * noise(q * vec2(5.0, 19.0)) + 0.12 * wave);
            float bank = smoothstep(0.223, 0.233, x);
            vec4 paving = mix(color3, color4, 0.16 + 0.13 * noise(q * 53.0));
            float flags = abs(fract(y * 13.0 + 0.5 * mod(floor(x * 23.0), 2.0)) - 0.5) / 13.0;
            paving = mix(paving, color4, coverage(flags, 0.003) * 0.37);
            result = mix(result, paving, bank);
            result = mix(result, color1, coverage(abs(x - 0.255), 0.008));
            for (int i = 0; i < 2; i++) {
                float top = -0.245 + 0.49 * float(i);
                float gate = segment(vec2(x, y), vec2(0.223, top), vec2(0.0, top + offset));
                result = mix(result, color4, coverage(gate, 0.021));
                result = mix(result, color3, coverage(gate, 0.005));
                float rail = segment(vec2(x, y), vec2(0.23, top), vec2(0.37, top - offset * 0.6));
                result = mix(result, color1, coverage(rail, 0.010));
            }
        """.trimIndent()

        PatternTextureType.FunicularIncline -> """
            vec2 q = fract(p + 0.5) - 0.5;
            float hill = 0.13 - 0.62 * q.x + 0.075 * sin(q.x * 8.0);
            result = mix(color1, color2, 0.45 * smoothstep(-0.43, 0.39, q.y));
            result = mix(result, color2, smoothstep(hill - pixelWidth, hill + pixelWidth, q.y));
            vec2 track = rotatePoint(q, 0.515);
            float rail = track.y - 0.140;
            result = mix(result, color3, coverage(abs(rail), 0.025));
            float piers = abs(fract(track.x * 7.0 - track.y * 3.8 + 0.5) - 0.5) / 7.0;
            result = mix(result, color3, coverage(piers, 0.012) * step(0.14, track.y) * 0.8);
            vec2 car = q - rotatePoint(vec2(offset, 0.11), -0.515);
            float sill = -0.56 * car.x;
            float shell = max(abs(car.x) - 0.19, max(sill - 0.141 - car.y, car.y - sill));
            result = mix(result, color4, coverage(shell, 0.0));
            float pane = max(abs(fract((car.x + 0.18) * 13.7) - 0.5) - 0.345,
                             max((sill - 0.12 - car.y) * 13.7, (car.y - sill + 0.057) * 13.7));
            result = mix(result, color3, coverage(shell, -0.009) * coverage(pane / 13.7, 0.0));
            result = mix(result, color1, coverage(abs(car.y - sill + 0.031), 0.006) * coverage(shell, -0.004));
        """.trimIndent()

        PatternTextureType.CinemaMarquee -> """
            vec2 q = fract(p + 0.5) - 0.5;
            vec2 vanishing = vec2(0.22, -0.25);
            vec2 v = q - vanishing;
            float y = abs(v.y) + 0.065;
            float blade = v.x + 0.28 * v.y;
            result = mix(color1, color2, 0.4 * noise(q * vec2(5.0, 9.0)));
            float signs = abs(fract((v.x + v.y * 0.20) * 8.0) - 0.5);
            result = mix(result, color3, 0.22 * step(0.26, signs) * smoothstep(0.0, 0.7, q.y + 0.28));
            for (int i = 0; i < 3; i++) {
                float facade = float(i);
                vec2 building = q - vec2(-0.40 + facade * 0.24, 0.0);
                float roof = 0.16 - 0.32 * hash(vec2(facade, 9.0));
                if (abs(building.x) < 0.105 && q.y > roof) {
                    vec2 pane = abs(fract(vec2(building.x * 22.0, (q.y - roof) * 17.0)) - 0.5);
                    result = mix(color2, color1, 0.69 + 0.12 * facade);
                    result = mix(result, color3, coverage(max(pane.x - 0.20, pane.y - 0.25) / 20.0, 0.0) * 0.61);
                }
            }
            if (v.y > 0.0 && blade < 0.09 + v.y * 0.36 && blade > -0.16 - v.y * 0.89) {
                float lane = (blade / y + 0.93) * 4.7;
                float crossbeam = log(y) * 4.5;
                vec2 fixture = fract(vec2(lane, crossbeam)) - 0.5;
                float flank = min(abs(blade - 0.09 - v.y * 0.36), abs(blade + 0.16 + v.y * 0.89));
                result = mix(color2, color3, 0.44 + 0.22 * cos(lane * TAU));
                float bulb = length(fixture * vec2(0.88, 1.18));
                result = mix(result, color4,
                             min(1.0, intensity * (0.12 * exp(-5.2 * bulb) +
                                                   coverage(bulb * y / 4.5, 0.175 * y / 4.5))));
                result = mix(result, color4, coverage(flank, 0.0045));
            }
        """.trimIndent()

        PatternTextureType.SolarProminences -> """
            vec2 q = vec2(p.x, -p.y);
            float radius = 0.91, disk = length(q - vec2(-0.13, -1.02)) - radius;
            float granules = noise(q * 52.0) * 0.58 + noise(q * 145.0) * 0.42;
            float turbulence = noise(q * 17.0 + time * 0.18) - 0.5;
            float rim = disk + turbulence * 0.009;
            result = mix(color1, color2, exp(-max(rim, 0.0) * 14.0) * (0.18 + granules * 0.30));
            for (int i = 0; i < 3; i++) {
                float n = float(i), angle = 1.16 + n * 0.34 - step(1.5, n) * 0.065;
                vec2 normal = vec2(cos(angle), sin(angle)), tangent = vec2(normal.y, -normal.x);
                vec2 delta = q - (vec2(-0.13, -1.02) + normal * radius);
                vec2 local = vec2(dot(delta, tangent), dot(delta, normal));
                local.x += 0.008 * sin(local.y * (29.0 + n * 6.0) - time + n);
                float arch = length(vec2((local.x + local.y * 0.24) / (0.080 + n * 0.018),
                                         local.y / (0.13 + 0.085 * sin(0.4 + n * 1.45))));
                float threads = arch + turbulence * (0.075 + 0.20 * smoothstep(0.04, 0.22, local.y));
                float shell = exp(-pow((threads - 1.0) * 3.1, 2.0)) * smoothstep(-0.03, 0.012, local.y);
                float filament = 0.5 + 0.5 * cos(65.0 * threads + noise(q * 28.0) * 4.0 + time);
                result = mix(result, color3, shell * (0.32 + 0.53 * filament));
                result = mix(result, color4, shell * pow(filament, 4.0) * (0.34 + 0.54 * granules));
            }
            vec4 chromosphere = mix(color2, color3, granules * 0.84 + 0.1);
            result = mix(result, chromosphere, coverage(rim, 0.0));
            result = mix(result, color4, exp(-abs(rim) * 110.0) * (0.52 + 0.45 * granules));
        """.trimIndent()

        PatternTextureType.StellarBowShock -> """
            vec2 q = rotatePoint(vec2(p.x, -p.y), -0.47);
            float dust = noise(q * 6.4) * 0.58 + noise(q * 16.0) * 0.29 + 0.13 * noise(q * 47.0);
            float fine = noise(q * 62.0), curl = (dust - 0.50) * 0.033 * variability;
            float distance = (q.x - 0.255 + 1.5 * q.y * q.y + curl) / sqrt(1.0 + 9.0 * q.y * q.y);
            float envelope = exp(-3.5 * q.y * q.y);
            result = mix(color1, color2, dust* dust * 0.39);
            float wake = exp(-pow((distance + 0.072) * 10.0, 2.0)) * envelope;
            result = mix(result, color2, wake * (0.32 + 0.68 * dust));
            for (int i = 0; i < 4; i++) {
                float n = float(i);
                float filament = distance + n * (0.015 + 0.04 * q.y * q.y);
                float light = exp(-abs(filament) * (77.0 + 24.0 * n)) * envelope;
                result = mix(result, mix(color3, color4, 0.32 * fine),
                             light * (0.88 - n * 0.12) * (0.58 + fine * 0.42));
            }
            vec2 cell = floor(q * 41.0), dotPos = (fract(q * 41.0) - hash2(cell)) / 41.0;
            float stars = step(0.915, hash(cell + 4.5)) * coverage(length(dotPos), 0.0018);
            result = mix(result, color4, stars * 0.68);
            vec2 star = q - vec2(0.045, -0.017);
            float core = exp(-length(star) * 88.0);
            float spikes = exp(-min(abs(star.x), abs(star.y)) * 440.0 - length(star) * 43.0);
            result = mix(result, color4, clamp(core * 1.45 + spikes * 0.58, 0.0, 1.0));
        """.trimIndent()

        PatternTextureType.HourglassNebula -> """
            vec2 q = rotatePoint(vec2(p.x, -p.y), 0.27);
            float gas = noise(q * 8.0) * 0.54 + noise(q * 22.0) * 0.32 + noise(q * 57.0) * 0.14;
            result = mix(color1, color2, gas* gas * 0.37);
            float height = abs(q.y);
            float width = (0.014 + 0.89 * height) * (0.87 + 0.09 * cos(height * 39.0)) * shapeAspect;
            float tangent = abs(q.x) - width - (gas - 0.5) * 0.032;
            float extent = 1.0 - smoothstep(0.26, 0.42, height);
            float inside = 1.0 - smoothstep(-0.02, 0.02, tangent);
            float ribs = 0.5 + 0.5 * sin(height * 110.0 + gas * 7.0 + 2.0 * q.x / max(width, 0.025));
            float edge = exp(-abs(tangent) * 49.0);
            result = mix(result, color2, extent* inside * (0.2 + 0.51 * gas));
            result = mix(result, color3, extent*(edge * 0.68 + inside * ribs * 0.29));
            result = mix(result, color4, extent* edge * (0.23 + 0.63 * ribs) * gas);
            float mouth = abs(length(vec2(q.x / (0.253 * shapeAspect), (height - 0.30) / 0.05)) - 1.0);
            result = mix(result, color3, exp(-mouth * 6.5) * (0.26 + gas * 0.52));
            float star = exp(-length(q) * 80.0);
            result = mix(result, color4, clamp(star * 1.8, 0.0, 1.0));
        """.trimIndent()

        PatternTextureType.RocheLobes -> """
            vec2 q = rotatePoint(vec2(p.x, -p.y), -0.23) * 3.3;
            float mu = balance;
            vec2 primary = vec2(-mu, 0.0), secondary = vec2(1.0 - mu, 0.0);
            vec2 a = q - primary, b = q - secondary;
            float ra = max(length(a), 0.07), rb = max(length(b), 0.057);
            float potential = (1.0 - mu) / ra + mu / rb + dot(q, q) * 0.5;
            vec2 gradient = q - (1.0 - mu) * a / (ra * ra * ra) - mu * b / (rb * rb * rb);
            float middle = -mu + 1.0 / (1.0 + pow(mu / (1.0 - mu), 0.4));
            float critical = (1.0 - mu) / (middle + mu) + mu / (1.0 - mu - middle) + middle * middle * 0.5;
            float d = (potential - critical) / max(length(gradient), 0.055);
            float grid = min(abs(fract(q.x * 3.0 + 0.5) - 0.5), abs(fract(q.y * 3.0 + 0.5) - 0.5));
            result = mix(color1, color2, coverage(grid / 10.0, 0.0011) * 0.36);
            float levels =
                    abs(fract((potential - critical) * 5.5 + 0.5) - 0.5) / max(length(gradient) * 18.15, 0.04);
            float cutoff = smoothstep(0.12, 0.32, min(ra, rb));
            result = mix(result, color2, cutoff* coverage(levels, 0.0024));
            result = mix(result, color3, coverage(abs(d) / 3.3, 0.004));
            result = mix(result, color4, coverage(ra, 0.125 + 0.055 * (1.0 - mu)));
            result = mix(result, color3, coverage(rb, 0.055 + 0.065 * mu));
        """.trimIndent()

        PatternTextureType.BasaltColonnade -> """
            vec2 q = vec2(p.x, -p.y);
            float grain = noise(q * 71.0);
            result = mix(color1, color3, 0.28 + 0.28 * q.y + 0.06 * grain);
            for (int j = 0; j < 6; j++) {
                float row = float(j);
                float spacing = 0.107 + 0.014 * row;
                float shift = row * 0.39;
                float column = floor(q.x / spacing + shift + 0.5);
                float x = (q.x / spacing + shift - column) * spacing;
                float h = 0.29 - row * 0.092 + 0.089 * sin(column * 0.52 + row * 0.39);
                h += variability * 0.11 * (hash(vec2(column, row)) - 0.5);
                float cap = 0.026 + 0.006 * row;
                float across = abs(x) / (spacing * 0.5);
                float front = h - cap * (1.0 - 0.48 * across);
                float top = h + cap * (1.0 - 0.48 * across);
                float side = coverage(q.y - front, 0.0);
                vec4 flank = mix(color1, color2, 0.23 + 0.28 * row / 5.0);
                flank = mix(flank, color3, (x > 0.0 ? 0.26 : 0.075) + 0.10 * grain);
                float seam = coverage(1.0 - across, 0.033);
                flank = mix(flank, color1, seam * 0.78);
                float stria = pow(0.5 + 0.5 * cos(x * 620.0 + noise(q * 12.0) * 2.2), 7.0);
                flank = mix(flank, color3, stria * 0.11);
                result = mix(result, flank, side);
                float roof = (1.0 - side) * coverage(q.y - top, 0.0);
                vec4 stone = mix(color3, color4, 0.24 + 0.41 * hash(vec2(column, row) + 8.0));
                result = mix(result, stone, roof * (0.94 + 0.06 * grain));
                float frontEdge = coverage(abs(q.y - front), 0.002 + 0.0005 * row);
                result = mix(result, color4, frontEdge * 0.6);
            }
        """.trimIndent()

        PatternTextureType.FaultedStrata -> """
            vec2 q = vec2(p.x, -p.y);
            float grit = noise(q * 97.0);
            float fault = q.x - q.y * 0.51 + (noise(q * 6.0) - 0.5) * 0.026;
            float right = smoothstep(-pixelWidth, pixelWidth, fault);
            float bend = 0.038 * sin(q.x * 5.8) + 0.075 * q.x;
            float drag = 0.042 * exp(-abs(fault) * 10.0) * (1.0 - 2.0 * right);
            float depth = q.y - bend + right * offset + drag + 0.013 * (noise(q * 11.0) - 0.5);
            float course = floor(depth * 16.0), local = fract(depth * 16.0);
            float id = hash(vec2(course, 1.5));
            vec4 rock = id < 0.32 ? color2 : (id < 0.71 ? color3 : color4);
            float laminations = 0.5 + 0.5 * cos(local * TAU * (2.0 + floor(id * 4.0)) + 0.5 * noise(q * 36.0));
            result = mix(rock, color1, 0.14 + 0.20 * (1.0 - grit) + 0.19 * laminations);
            float contact = min(local, 1.0 - local) / 16.0;
            result = mix(result, color1, coverage(contact, 0.0026) * 0.8);
            result = mix(result, color1, coverage(abs(fault), 0.0052));
            result = mix(result, color4, coverage(abs(fault + 0.007), 0.0016) * 0.72);
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
            vec2 q = rotatePoint(vec2(p.x, -p.y), -0.29);
            float sediment = noise(p * 12.0) * 0.47 + noise(p * 51.0) * 0.35 + noise(p * 155.0) * 0.18;
            result = mix(color2, color3, 0.05 + sediment * 0.71);
            float head = ellipseDistance(q - vec2(0.0, 0.19), vec2(0.226, 0.137));
            head = max(head, 0.12 - q.y - abs(q.x) * 0.12);
            float body = ellipseDistance(q - vec2(0.0, -0.003), vec2(0.18, 0.326));
            body = max(body, q.y - 0.177);
            float tip = ellipseDistance(q - vec2(0.0, -0.28), vec2(0.091, 0.091));
            float spine = segment(vec2(abs(q.x), q.y), vec2(0.207, 0.168), vec2(0.242, 0.030));
            float silhouette = min(min(head, body), min(tip, spine - 0.005));
            float height = max(0.0, 1.0 - pow(abs(q.x) / 0.22, 2.0));
            float stipple = noise(q * 141.0);
            vec4 shell = mix(color1, color2, (0.18 + 0.46 * height) * (0.68 + 0.44 * sediment));
            result = mix(result, color4, coverage(silhouette + 0.003 * stipple, 0.006) * (0.48 + 0.35 * sediment));
            result = mix(result, shell, coverage(silhouette, 0.0));
            float row = q.y + 0.59 * abs(q.x) - 0.030 * sin(abs(q.x) * 18.0);
            float spacing = 0.034;
            float ridge = abs(fract(row / spacing + 0.5) - 0.5) * spacing;
            float thorax = coverage(body, -0.004) * (1.0 - smoothstep(0.10, 0.13, q.y));
            result = mix(result, color1, coverage(abs(ridge - 0.006), 0.0022) * thorax * 0.56);
            result = mix(result, color3, coverage(ridge, 0.0048) * thorax * intensity * (0.70 + 0.28 * stipple));
            float axis = ellipseDistance(q - vec2(0.0, -0.018), vec2(0.060, 0.283));
            result = mix(result, color2, coverage(axis, 0.0) * coverage(body, 0.0));
            float rib = abs(fract((q.y + 0.46 * q.x * q.x) / spacing + 0.5) - 0.5) * spacing;
            result = mix(result, color4, coverage(rib, 0.0037) * coverage(axis, -0.001) * thorax * intensity);
            float glabella = ellipseDistance(q - vec2(0.0, 0.195), vec2(0.075, 0.090));
            result = mix(result, color3, coverage(glabella, 0.0) * (0.38 + 0.35 * sediment));
            vec2 eyePos = vec2(abs(q.x) - 0.127, q.y - 0.195);
            float eye = ellipseDistance(rotatePoint(eyePos, -0.47), vec2(0.027, 0.047));
            result = mix(result, color1, coverage(eye, 0.0) * 0.67);
            vec2 lens = fract(eyePos * 121.0 + vec2(step(0.5, fract(eyePos.y * 60.5)) * 0.5, 0.0)) - 0.5;
            result = mix(result, color3, coverage(length(lens) / 121.0, 0.002) * coverage(eye, -0.002) * 0.66);
            result = mix(result, color3, coverage(abs(eye), 0.003) * 0.59);
            float cheek = ellipseDistance(q - vec2(0.0, 0.205), vec2(0.180, 0.096));
            result = mix(result, color2,
                         coverage(abs(cheek), 0.003) * smoothstep(0.08, 0.135, abs(q.x)) * coverage(head, -0.003));
        """.trimIndent()

        PatternTextureType.DoubleSlit -> """
            vec2 q = vec2(p.x + 0.21, p.y);
            float separation = radius;
            float wavelength = 0.049;
            float phase = time;
            float incident = 0.5 + 0.5 * cos(q.x * TAU / wavelength - phase);
            float a = length(q - vec2(0.0, separation)), b = length(q + vec2(0.0, separation));
            float wave = 0.5 * (cos(a * TAU / wavelength - phase) + cos(b * TAU / wavelength - phase));
            float visibility = 1.0 / (1.0 + 1.3 * (a + b));
            vec4 left = mix(color1, color2, incident * 0.73 + 0.08);
            left = mix(left, color3, pow(incident, 10.0) * 0.53);
            vec4 right = mix(color1, color2, 0.46 + wave * 0.42);
            right = mix(right, color3, smoothstep(0.12, 0.86, wave) * min(1.0, 1.6 * visibility));
            right = mix(right, color4, smoothstep(0.78, 1.0, wave) * visibility);
            result = mix(left, right, smoothstep(-pixelWidth, pixelWidth, q.x));
            float slit = abs(abs(q.y) - separation);
            float barrier = coverage(abs(q.x), 0.007) * (1.0 - coverage(slit, 0.015));
            result = mix(result, color4, barrier * 0.93);
        """.trimIndent()

        PatternTextureType.LensCrossSection -> """
            vec2 q = vec2(p.x, -p.y);
            float graticule = min(abs(fract(q.x * 16.0 + 0.5) - 0.5), abs(fract(q.y * 16.0 + 0.5) - 0.5)) / 16.0;
            result = mix(color1, color2, 0.14 * coverage(graticule, 0.0014));
            float axis = coverage(abs(q.y), 0.0013) * step(0.32, fract(q.x * 21.0));
            result = mix(result, color3, axis * 0.47);
            for (int i = 0; i < 3; i++) {
                float n = float(i), x = -0.245 + n * 0.164;
                float aperture = 0.259 - n * 0.043;
                float height = q.y / aperture, bend = 1.0 - min(height * height, 1.0);
                float left = x - (0.014 + bend * (i == 1 ? -0.011 : 0.041));
                float right = x + 0.026 + bend * (i == 1 ? 0.043 : 0.012);
                float lens = max(max(left - q.x, q.x - right), abs(q.y) - aperture);
                result = mix(result, color2, coverage(lens, 0.0) * (0.47 + 0.38 * bend));
                result = mix(result, color3, coverage(abs(lens), 0.0029) * 0.93);
                float mount = max(abs(q.x - x) - 0.033, abs(abs(q.y) - aperture - 0.022) - 0.021);
                result = mix(result, color2, coverage(mount, 0.0));
            }
            for (int i = -2; i <= 2; i++) {
                if (i == 0) continue;
                float y = float(i) * 0.079;
                vec2 a = vec2(-0.245, y), b = vec2(-0.080, y * 0.86), c = vec2(0.082, y * 0.69);
                vec2 focus = vec2(offset, 0.0);
                float ray = min(segment(q, vec2(-6.0, y), a), segment(q, a, b));
                ray = min(ray, segment(q, b, c));
                ray = min(ray, segment(q, c, focus + (focus - c) * 9.0));
                result = mix(result, color4, coverage(ray, 0.0024) * 0.91);
            }
        """.trimIndent()

        PatternTextureType.IrisDiaphragm -> """
            vec2 q = rotatePoint(vec2(p.x, -p.y), 0.26);
            float r = length(q), a = safeAngle(q);
            float blades = count, sector = TAU / blades;
            float index = floor(a / sector + 0.5);
            float angular = a - index * sector;
            float aperture = r * cos(angular) - radius;
            float bladePhase = fract(a / sector - r * 2.8 + 0.18);
            float edge = min(bladePhase, 1.0 - bladePhase) * sector * max(r, 0.02);
            float scuff = noise(vec2(r * 120.0, q.x * 14.0 - q.y * 22.0));
            float sheen = 0.5 + 0.5 * cos(index * sector - 0.66 + 0.85 * bladePhase);
            vec4 blade = mix(color2, color3, 0.09 + 0.51 * sheen + 0.12 * scuff);
            result = color1;
            float ring = coverage(r, 0.443);
            result = mix(result, blade, ring * (1.0 - coverage(aperture, 0.0)));
            result = mix(result, color1, ring* coverage(edge, 0.0032) * (1.0 - coverage(aperture, 0.0)));
            result = mix(result, color4, coverage(abs(aperture), 0.0027) * 0.82);
            result = mix(result, color2, coverage(abs(r - 0.447), 0.025));
            result = mix(result, color3, coverage(abs(r - 0.462), 0.0055));
            float knurl = abs(fract(a / TAU * 90.0 + 0.5) - 0.5);
            result = mix(result, color4, coverage(abs(r - 0.448), 0.009) * coverage(knurl * 0.031, 0.002));
            result = mix(result, color4, coverage(abs(r - 0.421), 0.0021));
        """.trimIndent()

        PatternTextureType.EmissionSpectrum -> """
            vec2 q = vec2(p.x, -p.y);
            float plate = floor(q.y * 3.40 + 0.5), vertical = fract(q.y * 3.40 + 0.5) - 0.5;
            float stroke = 0.0, halo = 0.0;
            float wavelength = q.x * 30.0;
            float bin = floor(wavelength);
            for (int i = -1; i <= 1; i++) {
                float k = bin + float(i);
                float center = k + 0.09 + 0.79 * hash(vec2(k, plate));
                float strength = hash(vec2(k, plate) + 4.3);
                float delta = abs(wavelength - center);
                float width = thickness * (0.46 + strength);
                float main = exp(-pow(delta / max(width, 0.025), 2.0));
                float pair = exp(-pow((delta - 0.16) / 0.041, 2.0)) * step(0.71, strength) * 0.51;
                stroke = max(stroke, (main + pair) * (0.18 + 0.82 * strength));
                halo += strength * exp(-delta * 4.5);
            }
            float t = 0.5 + 0.5 * sin(q.x * 4.6);
            vec4 hue = t < 0.50 ? mix(color2, color3, t * 2.0) : mix(color3, color4, t * 2.0 - 1.0);
            float slit = 1.0 - smoothstep(0.26, 0.40, abs(vertical));
            result = mix(color1, hue, clamp((0.027 + halo * 0.30 + stroke * 0.98) * slit, 0.0, 1.0));
            float ruler = coverage(abs(q.y - (plate - 0.40) / 3.40), 0.0013);
            float tick = coverage(abs(fract(q.x * 30.0 + 0.5) - 0.5) / 30.0, 0.0011);
            result = mix(result, color3, (ruler + tick * coverage(abs(vertical + 0.36), 0.027)) * 0.44);
        """.trimIndent()

        PatternTextureType.SchlierenPlume -> """
            vec2 q = vec2(p.x, -p.y);
            float rise = max(q.y + 0.365, 0.0);
            float width = 0.016 + 0.21 * rise;
            float n = noise(vec2(q.x * 11.0, rise * 7.3) + time * 0.16);
            float flow = 0.70 * sin(rise * 14.0 - time) + 0.47 * sin(rise * 26.0 + 1.1 + n * 3.4);
            float transverse = (q.x - rise * flow * 0.165) / width;
            float folds = 0.23 * sin(rise * 39.0 + transverse * 1.8 + n * 3.0);
            float signedEdge = transverse + folds;
            float left = exp(-pow((signedEdge + 0.70) / 0.23, 2.0));
            float right = exp(-pow((signedEdge - 0.63) / 0.30, 2.0));
            float neck = smoothstep(-0.008, 0.014, q.y + 0.365);
            float inside = exp(-transverse * transverse * 0.86);
            float veil = 0.5 + 0.5 * sin(transverse * 14.0 + rise * 27.0 + 11.0 * n);
            result = mix(color1, color4, 0.08 + inside * neck * (0.11 + 0.41 * veil));
            result = mix(result, color2, left* neck * (0.53 + n * 0.38));
            result = mix(result, color3, right* neck * (0.53 + n * 0.34));
            vec2 burner = q - vec2(0.0, -0.398);
            float housing = max(abs(burner.x) - 0.034, abs(burner.y) - 0.036);
            result = mix(result, color2, coverage(housing, 0.0));
            result = mix(result, color3, coverage(segment(q, vec2(-0.032, -0.363), vec2(0.032, -0.363)), 0.004));
        """.trimIndent()

        PatternTextureType.HandRadiograph -> """
            vec2 q = rotatePoint(vec2(p.x, -p.y), -0.09);
            float tissue = 0.0, medulla = 0.0, cortex = 0.0;
            for (int i = 0; i < 5; i++) {
                float n = float(i);
                vec2 start, dir;
                float reach;
                if (i == 0) {
                    start = vec2(-0.093, -0.188);
                    dir = normalize(vec2(-0.79, 0.64));
                    reach = 0.348;
                } else {
                    float finger = n - 1.0;
                    start = vec2(-0.080 + finger * 0.047, -0.174);
                    dir = normalize(vec2(-0.11 + finger * 0.125, 1.0));
                    reach = 0.61 - 0.038 * abs(finger - 1.0) - 0.023 * max(finger - 1.0, 0.0);
                }
                for (int j = 0; j < 4; j++) {
                    if (i == 0 && j == 3) break;
                    float joint = float(j);
                    float part = (j == 0 ? 0.41 : (j == 1 ? 0.265 : (j == 2 ? 0.183 : 0.142))) * reach;
                    if (i == 0) part = (j == 0 ? 0.46 : (j == 1 ? 0.32 : 0.22)) * reach;
                    vec2 end = start + dir * part;
                    vec2 shaft = end - start;
                    float k = clamp(dot(q - start, shaft) / dot(shaft, shaft), 0.0, 1.0);
                    float distance = length(q - mix(start, end, k));
                    float girth = (0.021 - joint * 0.0027) * (1.0 + 0.30 * pow(abs(2.0 * k - 1.0), 3.0));
                    float bone = segment(q, start + 0.023 * dir, end - 0.022 * dir) - girth;
                    tissue = max(tissue, coverage(distance, girth + 0.009));
                    medulla = max(medulla, coverage(bone, 0.0));
                    cortex = max(cortex, coverage(abs(bone + 0.003), 0.0034));
                    start = end + dir * 0.002;
                    dir = rotatePoint(dir, (n - 2.0) * (j == 0 ? -0.015 : 0.014));
                }
            }
            for (int i = 0; i < 7; i++) {
                float n = float(i), row = step(3.5, n);
                float col = n - row * 4.0;
                vec2 center = vec2((col - 1.5 + 0.5 * row) * 0.047, -0.209 - 0.037 * row + 0.007 * sin(n));
                vec2 local = rotatePoint(q - center, n * 1.13);
                float bone = ellipseDistance(local, vec2(0.021, 0.016));
                medulla = max(medulla, coverage(bone, 0.0));
                cortex = max(cortex, coverage(abs(bone + 0.002), 0.0025));
            }
            for (int i = 0; i < 2; i++) {
                float side = float(i);
                vec2 a = vec2(-0.043 + 0.099 * side, -0.53), b = vec2(-0.044 + 0.111 * side, -0.279);
                float width = 0.022 - 0.006 * side + 0.017 * smoothstep(-0.43, -0.28, q.y);
                float bone = segment(q, a, b) - width;
                medulla = max(medulla, coverage(bone, 0.0));
                cortex = max(cortex, coverage(abs(bone + 0.003), 0.003));
            }
            float palm = ellipseDistance(rotatePoint(q - vec2(-0.001, -0.070), -0.08), vec2(0.143, 0.153));
            tissue = max(tissue, coverage(palm, 0.0));
            float grain = noise(q * 92.0);
            result = mix(color1, color2, 0.06 + tissue * 0.47);
            result = mix(result, color3, medulla * (0.44 + grain * 0.27));
            result = mix(result, color4, cortex* intensity);
        """.trimIndent()

        PatternTextureType.LogisticBifurcation -> """
            vec2 uv = vec2(p.x + 0.5, 0.5 - p.y);
            float rate = 2.75 + uv.x * 1.25;
            vec2 grid = abs(fract(uv * vec2(12.5, 10.0) + 0.5) - 0.5) / vec2(12.5, 10.0);
            result = mix(color1, color2, coverage(min(grid.x, grid.y), 0.0011) * 0.50);
            if (rate >= 0.0 && rate <= 4.0 && uv.y >= -0.1 && uv.y <= 1.1) {
                float population = 0.423591;
                for (int i = 0; i < 64; i++) population = rate * population * (1.0 - population);
                float nearest = 1.0, visits = 0.0;
                for (int i = 0; i < 96; i++) {
                    population = rate * population * (1.0 - population);
                    float distance = abs(uv.y - population);
                    nearest = min(nearest, distance);
                    visits += exp(-distance * 260.0);
                }
                result = mix(result, color3, 1.0 - exp(-0.23 * visits));
                result = mix(result, color4, coverage(nearest, thickness) * (0.60 + 0.4 * min(visits, 1.0)));
            }
        """.trimIndent()
    }
    val uniforms = type.parameters.joinToString("\n") { "uniform float ${it.name};" }
    val seed = if (type.parameters.any { it.name == "seed" }) "" else "const float seed = 0.0;"
    return """
        precision highp float;
        varying vec2 textureCoordinate;
        uniform vec2 aspect;
        uniform float frequency, offsetX, offsetY, rotation, pixelWidth;
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
        void main() {
            vec2 p=(textureCoordinate-.5)*aspect-vec2(offsetX,offsetY);
            float a=radians(rotation);p=mat2(cos(a),-sin(a),sin(a),cos(a))*p*frequency;
            vec4 result=color1;
            { $body }
            gl_FragColor=vec4(result.rgb*result.a,result.a);
        }
    """.trimIndent()
}