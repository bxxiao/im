package com.bx.im.controller;

import com.bx.im.util.CommonResult;
import com.bx.im.util.SpringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class UploadController {

    @PostMapping("upload")
    public CommonResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        String path = "D:\\upload\\";
        File newFile = new File(path, file.getOriginalFilename());
        file.transferTo(newFile);

        return CommonResult.success();
    }

}
