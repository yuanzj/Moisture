# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.
- Always output answers in 中文

## Project Overview

This is a moisture measurement Android application written in Java that supports both Bluetooth and serial port communication with moisture measurement devices. The app allows users to measure moisture content, calibrate devices, view historical data, and export reports.

## Build and Development Commands

### 配置代理
```bash
export https_proxy=http://127.0.0.1:7890 http_proxy=http://127.0.0.1:7890 all_proxy=socks5://127.0.0.1:7891
```

### 配置 Java 环境
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

### Building the App
```bash
# Build debug version
./gradlew app:assembleDebug

# Build release version
./gradlew app:assembleRelease

# Clean build
./gradlew clean
```

### Running Tests
```bash
# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

### Installing on Device
```bash
# Install debug version
./gradlew installDebug

# Install release version
./gradlew installRelease
```

## Architecture Overview

The application follows MVP (Model-View-Presenter) pattern with the following key components:

### Core Architecture
- **MVP Pattern**: Each major feature has Contract, Model, Presenter, and Activity classes
- **Dependency Injection**: Services are injected through the App singleton
- **Event-Driven Communication**: Uses EventBus for cross-component communication
- **Data Layer**: Abstracted through service interfaces with local and Bluetooth implementations

### Multi-Module Structure
- **app**: Main application module
- **bluetoothspp**: Bluetooth SPP communication library
- **serialportlib**: Serial port communication library (with native JNI)
- **rkconverter**: Data conversion utilities
- **MPChartLib**: Chart rendering library
- **zjandroidcommon**: Common base classes and utilities

### Key Components
- **App.java**: Application singleton managing services and auto-connect functionality
- **BluetoothBaseActivity**: Base class for activities requiring Bluetooth connectivity
- **LocalDataService**: Local data storage and retrieval
- **BluetoothService**: Device communication abstraction
- **Data Models**: Located in `data/` package with protocol-specific request/response classes

### Communication Protocols
- **Bluetooth**: SPP (Serial Port Profile) and BLE support
- **Serial Port**: Native serial communication via JNI
- **Protocol**: Custom binary protocol with CRC16 validation

### Main Activities
- **MainActivity**: Home screen with navigation to main features
- **DashboardActivity**: Multi-point measurement dashboard
- **MeasureActivity**: Single-point measurement interface
- **CorrectActivity**: Device calibration interface
- **ReportActivity**: Historical data and export functionality
- **SettingActivity**: App and device configuration

## Key Features

### Dual Communication Support
The app supports both Bluetooth and serial port connections (connectedModel: 0=serial, 1=bluetooth).

### Auto-Start Functionality
Automatic measurement scheduling based on device timing configuration with background monitoring.

### Data Management
- Local SQLite storage via LocalDataService
- Excel export functionality
- Historical data visualization using MPChart

### Device Configuration
- Multi-point measurement support (1-5 points)
- Calibration modes (single/two-point, NaCl/MgCl)
- Measurement parameters and timing settings

## Development Notes

### Communication Flow
1. Device discovery and connection
2. Protocol handshake and status queries
3. Parameter configuration and validation
4. Measurement execution and data collection
5. Data processing and storage

### Testing Strategy
- Unit tests for data models and utilities
- Integration tests for Bluetooth communication
- UI tests for major user flows

### Build Configuration
- Uses Android Gradle Plugin 4.1.1
- Targets API 30 with minimum API 23
- ProGuard disabled for easier debugging
- Signed builds using keystore configuration