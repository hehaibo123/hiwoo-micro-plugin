// enums.ts

 const deviceTypesMap: Record<string, string> = {
    '1': '真实设备',
    '2': '模拟设备',
    '3': '就源输入型设备',
    // 添加更多映射关系
};
// 其他类型映射
 const deviceStatusMap: Record<string, string> = {
    '1': '在线',
    '2': '离线',
    '3': '报警',
    // 添加更多其他类型映射关系
};
const deviceWayMap: Record<string, string> = {
    '0': '-',
    '1': '方式1',
    '2': '方式2',
    '3': '方式3',
    // 添加更多其他类型映射关系
};

const mapDeviceSource: Record<string, string> = {
    '0': '-',
    '1': 'bool',
    '2': 'int',
    '3': 'float',
    '4': 'string',
    '5': 'double',
    // 添加更多其他类型映射关系
};
const variableDataTypeMap: Record<string, string> = {
    '1': 'bool',
    '2': 'int',
    '3': 'float',
    '4': 'string',
    '5': 'double',
    // 添加更多其他类型映射关系
};

export { deviceTypesMap, deviceStatusMap ,deviceWayMap,variableDataTypeMap,mapDeviceSource};