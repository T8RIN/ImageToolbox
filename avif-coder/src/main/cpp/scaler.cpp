//
// Created by Radzivon Bartoshyk on 01/01/2023.
//

#include "scaler.h"

std::pair<int, int> resizeAspect(std::pair<int, int> sourceSize, std::pair<int, int> dstSize) {
    int sourceWidth = sourceSize.first;
    int sourceHeight = sourceSize.second;
    auto isHorizontal = sourceWidth > sourceHeight;
    std::pair<int, int> targetSize = isHorizontal ? dstSize : std::pair<int, int>(dstSize.second,
                                                                                  dstSize.first);
    int targetWidth = targetSize.first;
    int targetHeight = targetSize.second;
    if (targetHeight > sourceHeight && targetWidth > sourceWidth) {
        return sourceSize;
    }
    float imageAspectRatio = (float) sourceWidth / (float) sourceHeight;
    float canvasAspectRation = (float) targetWidth / (float) targetHeight;
    float resizeFactor;
    if (imageAspectRatio > canvasAspectRation) {
        resizeFactor = (float) targetWidth / (float) sourceWidth;
    } else {
        resizeFactor = (float) targetHeight / (float) sourceHeight;
    }
    std::pair<int, int> resultSize((int) ((float) sourceWidth * resizeFactor),
                                   (int) ((float) sourceHeight * resizeFactor));
    return resultSize;
}