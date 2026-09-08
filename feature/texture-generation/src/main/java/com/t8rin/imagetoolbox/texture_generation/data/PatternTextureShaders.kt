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
            for(int i=0;i<64;i++){
                if(float(i)>=count)break;
                float angle=float(i)*TAU/count;
                vec2 center=vec2(cos(angle),sin(angle))*.4;
                d=min(d,abs(length(p-center)-radius));
            }
            result=mix(color1,color2,1.0-smoothstep(thickness,thickness+.005,d));
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
                float a=fract(safeAngle(q)/TAU);
                result=a<.5?color2:color3;
                if(d<radius*.75)result=color4;
            }
        """.trimIndent()

        PatternTextureType.SquareRipplePattern -> """
            vec2 q=p+vec2(sin(p.y*1.2),sin(p.x*1.2))*offset;
            vec2 cell=floor(q),v=fract(q);
            result=mod(cell.x+cell.y,2.0)<1.0?color1:color2;
            if(min(min(v.x,1.0-v.x),min(v.y,1.0-v.y))<thickness)result=mod(cell.x+cell.y,2.0)<1.0?color3:color4;
        """.trimIndent()

        PatternTextureType.HatchPattern -> """
            result=mix(color1,color2,step(thickness,fract(p.x)));
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
            bool horizontal=abs(v.y)<thickness*.5;
            bool vertical=abs(v.x)<thickness*.5;
            result=color1;
            if(horizontal||vertical){
                bool top=horizontal&&(!vertical||parity<.5);
                float across=top?v.y:v.x;
                result=top?color2:color3;
                result.rgb*=1.0-shadows*pow(abs(across)*2.0/thickness,2.0)*.6;
                result.rgb*=.9+.1*cos(across*TAU*40.0);
            }
        """.trimIndent()

        PatternTextureType.PlaidPattern -> """
            float x=floor(p.x),y=floor(p.y);
            vec4 a=palette(hash(vec2(x,0))),b=palette(hash(vec2(y,0)));
            float over=step(.5,fract((p.x+p.y)*12.0));
            result=mix(a,b,over);
            if(fract(p.x)<thickness*.2||fract(p.y)<thickness*.2)result=mix(result,color1,.6);
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
    }
    val uniforms = type.parameters.joinToString("\n") { "uniform float ${it.name};" }
    val seed = if (type.parameters.any { it.name == "seed" }) "" else "const float seed = 0.0;"
    return """
        precision highp float;
        varying vec2 textureCoordinate;
        uniform vec2 aspect;
        uniform float frequency, offsetX, offsetY, rotation;
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