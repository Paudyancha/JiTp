# JiTp 项目代理指令

本文件在用户级 `~/.dsh/AGENTS.md` 全局规范之下生效，只补充 JiTp 项目的具体约束，冲突时以本文件为准。

## 项目概况

NeoForge 1.21.1 模组，Java 21，模组号 `jitp`，基础包 `dev.zanth.teleport`。源码只有三个文件：`JiTP`（模组入口）、`TeleportLimitHandler`（命令事件处理）、`TeleportCommandPermissionMixin`（Mixin）。`build.gradle` 的 `dependencies` 为空。

## 源码检索规则

1. 使用 `rg` 检索项目文件名与文件内容，使用命令行工具读取项目文件。
2. 本环境没有提供 IDEA MCP 工具与 `workspace-agent-bridge` 技能，检索不依赖这两者。
3. 源码文件数量很少，定位到目标文件后直接整文件读取。
4. 需要查看 Minecraft 与 NeoForge 实现时，检索 `~/.gradle/caches/` 下已缓存的源码包，禁止反编译二进制包。
5. 需要新增依赖时先向用户说明用途并取得确认，禁止为了检索源码引入依赖。

## 其他任务的工具偏好

检索与读取项目文件之外的操作用专用工具完成，例如格式化、重构、诊断与构建。当前环境没有提供 IDE 集成时，使用命令行工具与 Gradle 任务完成同样的操作。

## 空安全

1. 项目没有引入 JSpecify、`javax.annotation` 或 JetBrains 注解依赖，现有代码不含空安全注解。
2. 新增代码默认不添加空安全注解；确需使用时先引入依赖并取得用户确认，注解包按全局规范的优先级选择 JSpecify。
3. 标注为可空的值在解引用前必须做非空检查。

## 代码风格

1. 以根目录 `style.xml` 作为 Java 代码格式与命名风格的标准。
2. 禁止通配符 import，统一使用显式 import。
3. Mixin 注入的方法以 `jitp$` 前缀命名，避免与目标类成员重名。
4. 事件处理沿用 `TeleportLimitHandler` 的无状态静态方法写法。
5. 文本文件保持 UTF-8 编码，并保留文件原有的换行符格式。

## 验证

1. 修改 Java 源码后执行 `./gradlew.bat compileJava --console=plain`。
2. 涉及生成数据或静态资源时执行 `./gradlew.bat runData --console=plain`，并检查 `src/generated/resources` 的变化。
3. 提交前执行 `git diff --check` 与 `git status --short`，确认没有遗漏未跟踪文件。
