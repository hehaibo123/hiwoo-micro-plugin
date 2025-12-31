export declare type User<T = any> = {
    id: string;
    headSculpture: string;
    name: string;
    account: string;
    roleId: string;
    roleName: string;
    departmentId: string;
    departmentName: string;
    phone: string;
    email: string;
    notes: string;
    isLocked: number;
    enterpriseId: string;
    updateTime: T;
    createTime: T;
    headSculptureFile: File
};
