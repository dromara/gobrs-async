#!/usr/bin/env sh

# 确保脚本抛出遇到的错误
set -e

# 生成静态文件
npm run build

# 执行完该文件后 手动到 dist 下的index.html 中 将  /assets/ 全部替换 为以下地址
# https://cdn.jsdelivr.net/gh/dromara/gobrs-async.github.io/assets/

# 进入生成的文件夹
cd docs/.vuepress/dist

echo 'async.sizegang.cn' > CNAME

git init
git add -A
git commit -m "auto commit"

# github
git branch -m master gh-pages
git remote add origin git@github.com:Memorydoc/gobrs-async.github.io.git
git push -u origin gh-pages -f

cd -
rm -rf docs/.vuepress/dist
