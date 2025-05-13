本项目已经上传到Github，可以进行下载

地址https://github.com/vanishforever1/SA

可以使用豆包【AI编程】，将Github项目地址发送之后让Ai协助进行调试或改进。

---
这里的文档没有图片，还请参考PDF

本文档不够详细，如果刚接触Web项目，可以多在网上搜索帖子，解决问题。

<h1 id="Deq9x">一、硬件实验平台</h1>

- Win10专业版22H2
- CPU：i78809G
- 内存：16GB

---

配置需求不高，IDEA内存建议分配2个G，这样启动或终止项目时会更快些。特意写在前面，有条件的还是建议设置一下。

可以自己搜索一下教程，这里给个参考帖子

https://blog.csdn.net/weixin_45525272/article/details/136807228

<h1 id="I64FQ">二、开发配置软件环境</h1>

请<font style="background-color:#FBDE28;">务必</font>先参考开源项目SmartAdmin官网进行环境配置，安装并配置好所需的软件和环境

官方网址：https://smartadmin.vip

项目部署：https://smartadmin.vip/views/start/guide/ProjectInit.html

下面给出本人在部署时的版本号供参考
| 软件名称          | 版本信息                     |
|-------------------|-----------------------------|
| JDK               | 17                          |
| IntelliJ IDEA     | 2023.1.4 (Ultimate Edition) |
| MySQL             | 8.0.30                      |
| Navicat Premium   | 16.1.6                      |
| Node              | v20.16.0                    |

<h1 id="Cr7VR">三、运行操作步骤</h1>

<font style="background-color:#FBDE28;">项目运行前，请务必参考项目官网完成环境配置</font>

本项目为前后端分离项目。前端代码使用官网js版本，后端代码使用java17版本。

运行时请将前后端项目分别在两个IDEA进程中打开

前端项目：【smart-admin-web】

后端项目：【smart-admin-api】

---

<h2 id="BiULC">1.前端运行步骤</h2>

在IDEA中打开前端项目【smart-admin-web】

<h3 id="JfMuU">1.1安装jsencrypt.js</h3>

前端src/lib/encrypt.js中导入了jsencrypt，请检查是否安装，如未安装运行时会报错。



在IDEA【终端】命令行中输入以下命令进行安装

    npm install jsencrypt

【终端】如下图，左侧菜单默认添加的就有。如果IDEA中未显示，请自行搜索教程打开该窗口



<h3 id="pLzox">1.2连接后端接口</h3>

打开前端项目，根目录下找到配置文件【.env.development】，确认url为后端项目的本地端口（项目初始1024）



<h3 id="c8KZV">1.3启动前端</h3>

在IDEA【终端】命令行中输入以下命令

    npm run dev

如果启动失败请检查错误信息，进行解决。

---

启动成功如下图所示

点击【终端】中第一个地址即可访问系统前端网页





<h2 id="T8bs1">2.数据库配置</h2>

<h3 id="yLcvW">2.1导入数据库</h3>

打开【Navicat】

点击左上角【连接】-选择【MySQL】，这里的用户名密码是配置MySQL时的用户密码





以我的【连接】为例，右键【运行sql文件】



选择sql文件，确保编码格式为UTF8，点击【开始】。成功将表添加到了本地数据库中。

---

在IDEA中打开后端项目【smart-admin-api】

在右侧菜单栏目打开【数据库】—【数据源】——【MySQL】



输入用户名和密码，点击左下角的蓝色字体【测试连接】。没有问题的话会提示测试连接成功。点击【应用】



右侧刷新显示了MySQL数据源，点击【0/13】，选择刚才导入的表



<h3 id="DrK0w">2.2后端连接至数据库</h3>

打开【sa-base.yaml】，路径为【smart-admin-api\sa-base\src\main\resources\dev\sa-base.yaml】

修改数据库连接配置，确保表名、用户名、密码与你的设置一致。





<h2 id="s6lBc">3.后端运行步骤</h2>

在IDEA中打开后端项目【smart-admin-api】

等待项目下载资源，可以搜索一下如何配置maven阿里云镜像，速度快一些。

这里就不再赘述了，可以自行找帖子。

<h3 id="xleyD">3.1【服务】窗口</h3>

首次打开项目我们可以先添加【服务】窗口，方便我们启动项目



点击【添加服务】-【运行配置类型】然后在弹出的复选框中选择【Spring Boot】



<h3 id="FL5bb">3.2 启动项目</h3>

在服务窗口中选中【AdminApplication】后，点击绿色三角【运行】

等待项目构建完成后即可成功启动，启动成功后会出现项目端口号

如果没有出现端口号请在控制台检查错误信息。





<h2 id="uOMaP">4.切换加密算法</h2>

本系统支持两种对称式加密算法，可以在代码中进行切换，<font style="background-color:#FBDE28;">前后端代码均需进行修改</font>

<font style="background-color:#FDE6D3;">IDEA中双击Shift按键可以启用搜索功能，输入Encryptors即可快速定位到该代码片段</font>

<h3 id="YDkR2">4.1 在encrypt.js中修改注释</h3>

首先Encryptors中的两个算法中，都写了if语句，请确保只保留当前的启用的算法，另一个if语句需要注释或删除掉，以防报错。可以参考下图



<h3 id="OnkNt">4.2 前端修改encrypt.js</h3>

切换注释currentAlgorithm即可，参考下图。



<h3 id="z9k8D">4.3 后端修改@Service服务</h3>

【net/lab1024/sa/base/module/support/apiencrypt/service】目录下有AES和SM4两个接口

若启用SM4，【ApiEncryptServiceSmImpl.java】中就需要启用Service服务



与此同时【ApiEncryptServiceAesImpl.java】中的Service一定是注释掉的



<h3 id="E808T">4.4 密钥表需要修改算法启用状态，0禁用，1启用。</h3>

可以在IDEA打开后端项目，然后修改密钥表。也可以直接在Navicat中修改。

这里以IDEA中修改为例进行演示



通过修改0或1，来启用或禁用算法。

修改之后记得点击绿色小箭头，提交修改。



<h2 id="kQjhp">5.代码路径</h2>

与官网项目相比，修改或新增的的相关代码路径如下（可能不全）

方便将本项目中的新增功能移植到新版SmartAdmin系统中。

---

前端相关代码

src/lib/axios.js

src/lib/encrypt.js

src/views/support/KeyUpdate/KeyUpdate.vue【前端密钥更新页面】

这里顺便提一下，菜单页面直接启动项目后在前端页面模仿已有的菜单进行配置即可

src/views/system/login3/login.vue

src/App.vue

---

后端相关代码

【net/lab1024/sa/base/module/support/apiencrypt】

后端修改的代码基本都在这个目录中了

<h1 id="a6Jjc">四、项目演示</h1>

项目主要实现了密钥交换和密钥更新，演示视频在文件夹中。就不提供截图了

<h1 id="bDFRk">五、存在的问题</h1>

1、本项目供测试使用，前端代码中的某些控制台输出在系统实际运行时需要删除

2、本项目使用的SmartAdmin版本与官网最新版本存在差异，如果需要在最新版本中嵌入模块，可能需要额外进行修改。

3、建议还是使用官网的新版本，本项目使用的SmartAdmin版本有个落后的点，密码在存储到数据库时的哈希加密算法采用的还是MD5，比较落后了，官网新版本的则是采用了Argon2加密（大概是这个吧，有点忘了）

<h1 id="VGpBb">六、可能的报错参考</h1>

运行后端项目时可能会报错：<font style="background-color:#FBDE28;">端口占用</font>

注意，若无法运行命令请使用<font style="background-color:#FBDE28;">管理员身份</font>运行命令提示符

https://blog.csdn.net/weixin_43530798/article/details/124318641






