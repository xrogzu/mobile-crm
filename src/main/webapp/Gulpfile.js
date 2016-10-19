var fs = require('fs'),
    path = require('path'),
    gulp = require('gulp'),
    gutil = require('gulp-util'),
    uglify = require('gulp-uglify'),
    cleanCSS = require('gulp-clean-css'),
    rimraf = require('gulp-rimraf'),
    rename = require("gulp-rename");

var jsFolder = './js/wx/',
    cssFolder = './css/wx';
    imgFolder = './images/wx';
// 压缩js
gulp.task('min:js', function() {
    return gulp.src(['*.js', '**/*.js', '!*.min.js', '!**/*.min.js'], {
        cwd: jsFolder,
        base: jsFolder
    })
        .pipe(uglify())
        .pipe(rename(function(path) {
            path.extname = ".min.js"
        }))
        .pipe(gulp.dest(jsFolder));
});

// 压缩css
gulp.task('min:css', function() {
    return gulp.src(['*.css', '**/*.css', '!*.min.css', '!**/*.min.css'], {
        cwd: cssFolder,
        base: cssFolder
    })
        .pipe(cleanCSS())
        .pipe(rename(function(path) {
            path.extname = ".min.css"
        }))
        .pipe(gulp.dest(cssFolder));
});

// 图片压缩
gulp.task('min:img', function(cb) {
    var imagemin = require('gulp-imagemin');
    console.log('min:img');
    return gulp.src(['**'], {
            cwd: imgFolder,
            base: imgFolder
        })
        .pipe(imagemin())
        .pipe(gulp.dest(imgFolder));
});
// 清除发布目录
gulp.task('clean:js', function(cb) {
    return gulp.src(['*.min.js', '**/*.min.js', '!**/jquery.slides.min.js', '!**/zepto.min.js'], {
        read: false,
        cwd: jsFolder,
        base: jsFolder
    })
        .pipe(rimraf({
            force: true
        }));
});

// 清除发布目录
gulp.task('clean:css', function(cb) {
    return gulp.src(['*.min.css', '**/*.min.css'], {
        read: false,
        cwd: cssFolder,
        base: cssFolder
    })
        .pipe(rimraf({
            force: true
        }));
});

// 清除发布目录
gulp.task('clean', ['clean:js', 'clean:css']);

// 压缩js、css
gulp.task('min', ['min:js', 'min:css']);