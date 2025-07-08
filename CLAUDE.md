# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

- Always output answers in 中文
- 终端环境执行命令前先添加代理配置
```bash
export https_proxy=http://127.0.0.1:7890 http_proxy=http://127.0.0.1:7890 all_proxy=socks5://127.0.0.1:7891
```
- 当前Android项目基于Java 8
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH=$JAVA_HOME/bin:$PATH
```

## Build and Development Commands

**重要构建注意事项:**
- 某些第三方依赖库已不可用，需要手动处理
- NDK配置问题已临时禁用（serialportlib模块）
- 需要使用特定的JDK 8版本：`/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home`

### 构建环境设置
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

### Building the project
```bash
./gradlew build
```

### Building release APK
```bash
./gradlew assembleRelease
```

### Building debug APK
```bash
./gradlew assembleDebug
```

### Running tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Cleaning build
```bash
./gradlew clean
```

### Installing APK to device
```bash
./gradlew installDebug
./gradlew installRelease
```

### 已知构建问题

1. **NDK配置问题**: serialportlib模块的CMake构建已临时禁用
2. **不可用的依赖项**: 
   - `com.inuker.bluetooth:library:1.4.0` (BLE库)
   - `io.itimetraveler:pickerselector:0.0.0.2` (选择器库)
3. **JCenter仓库废弃**: 已更新为使用mavenCentral()
4. **编译错误**: BluetoothBaseActivity中的BLE相关代码需要替代方案

## Project Architecture

This is an Android application for moisture measurement using MVP (Model-View-Presenter) architecture pattern.

### Key Architecture Components

- **MVP Pattern**: Each feature module follows MVP architecture with Contract, Model, and Presenter classes
- **Bluetooth Communication**: Supports both BLE (Bluetooth Low Energy) and SPP (Serial Port Profile) connections
- **Serial Port Communication**: USB/Serial connectivity via custom serial port library
- **Data Management**: Local SQLite database with repository pattern
- **Multi-module Structure**: Organized as a multi-module Gradle project

### Module Structure

- `app/` - Main application module containing UI activities and business logic
- `bluetoothspp/` - Custom Bluetooth SPP communication library
- `serialportlib/` - Serial port communication library with JNI components
- `MPChartLib/` - Chart visualization library (modified version of MPAndroidChart)
- `rkconverter/` - Data conversion utilities
- `zjandroidcommon/` - Common Android utilities and base classes

### Key Features

- **Multi-point measurement**: Supports 1-5 measurement points with dashboard view
- **Data visualization**: Real-time charts using MPChart library
- **Auto-scheduled measurements**: Background service for automatic data collection
- **Data export**: Excel file generation and export functionality
- **Device configuration**: Parameter settings and calibration management
- **Dual connectivity**: Both Bluetooth and USB/Serial port connections

### Connection Models

The app supports two connection modes (controlled by `App.connectedModel`):
- `0`: Serial/USB connection via `/dev/ttyS4` at 115200 baud
- `1`: Bluetooth connection (BLE or SPP)

### Base Classes

- `BluetoothBaseActivity` - Base for activities requiring Bluetooth functionality
- `BaseMvpActivity` - MVP base activity with presenter lifecycle management
- `BasePresenter` and `BaseView` - MVP contract interfaces

### Data Flow

1. Device connection via Bluetooth or Serial port
2. Command/response protocol for device communication
3. Data parsing and storage in local database
4. UI updates via MVP presenter pattern
5. Real-time chart visualization and data export

### Important Notes

- All activities are locked to landscape orientation
- The app uses EventBus for decoupled communication
- Automatic measurement scheduling requires screen to be on
- Custom keystore configuration in gradle.properties for release builds