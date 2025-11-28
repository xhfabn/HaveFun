# Frontend Enhancement Plan

## Admin Portal
1. **Auth Guard**
   - Persist token in `localStorage` and redirect unauthenticated users to `/admin/login`.
   - Implement a wrapper component that checks `adminToken` before rendering nested routes.
2. **Navigation & Layout**
   - Sidebar entries for Employees, Categories, Dishes, Orders, and Dashboard summary.
   - Header should show logged-in name and logout button.
3. **Employees**
   - Table with pagination driven by `/admin/employee/page`.
   - Modal form for create/edit and status toggle calling `/status/{status}`.
4. **Categories**
   - CRUD UI talking to `/admin/category` endpoints.
   - Filter by type (1=菜品 2=套餐) when creating dishes/setmeals.
5. **Dishes**
   - Use `/admin/dish/page`, `/admin/dish`, `/admin/dish/{id}` for manage and `/admin/dish/status/{status}` toggle.
   - Image uploader placeholder (OSS integration hook).
6. **Orders Overview**
   - Basic list via `/admin/order/page` (if available) or fallback to `/admin/order/statistics`.

## Admin Portal

| 模块 | 后端 Controller/Endpoints | 前端页面现状 | 缺失/改进点 |
| --- | --- | --- | --- |
| 登录 & 布局 | `EmployeeController` (login) | `/admin/login`, `Layout` 已实现 | - |
| 员工管理 | `/admin/employee` CRUD + `/status/{status}` | `EmployeeList` 存在 | 校验规则、字段补全（性别、身份证号、手机号等），缺少重置密码入口 |
| 分类管理 | `/admin/category` 全量接口 | `CategoryList` 存在 | 需要支持 type=1/2 切换、排序字段、批量删除/禁用提示 |
| 菜品管理 | `/admin/dish` CRUD、口味、上下架 | `DishList` 已有基础表格 | 未集成图片上传、口味动态校验不足、缺少批量删除、缓存刷新提示 |
| 套餐管理 | `/admin/setmeal` CRUD、状态 | **缺页面** | 需要全新 `SetmealList` + 抽屉/表单，包含菜品勾选器（使用 `/admin/dish/list?cateId=`）|
| 订单管理 | `/admin/order` 条件搜索、详情、流转 | `OrderList` 基本完成 | 需补支付方式/订单金额展示格式、接单/拒单原因校验、批量导出；与工作台统计联动 |
| 工作台 | `/admin/workspace`（BusinessData, overview*） | `Dashboard` 仅使用 `order/statistics` | 需接入营业额/用户/菜品/套餐概览卡片、今日趋势图 |
| 运营报表 | `/admin/report/*` + `/export` | **缺页面** | 设计日期区间选择、折线/柱状图、TOP10 饼图、Excel 导出按钮 |
| 店铺设置 | `/admin/shop/status` | **缺页面** | 添加营业状态开关、自动刷新提示 |
| 通用上传 | `/admin/common/upload` | 客户端仅 placeholder | 需封装 `Upload` 组件调用 OSS 接口，供菜品/套餐图使用 |

## UI Data Models

### 员工管理（EmployeeList）
- 表格列：姓名、账号、手机号、性别、状态、创建时间。
- 搜索项：姓名/账号关键字、状态。
- 表单字段：`name`, `username`, `phone`, `sex`, `idNumber`（创建默认密码 123456）。
- 操作：新增/编辑/禁用，支持重置密码弹窗（调用后端预留接口）。

### 分类管理（CategoryList）
- 表格列：名称、类型（菜品/套餐）、排序、状态、更新时间。
- 表单字段：`type`, `name`, `sort`。
- 交互：切换启用、删除前确认，提供“菜品分类/套餐分类”标签。

### 菜品管理（DishList）
- 列：菜品名、分类、售价、状态、更新时间。
- 筛选：名称、分类(type=1)、状态。
- 表单：`name`, `categoryId`, `price`, `code`, `image`, `description`, `flavors`, `status`。
- 组件：`ImageUploader` 调 `/admin/common/upload`，`FlavorList` 动态增减口味项。

### 套餐管理（SetmealList）
- 列：套餐名、分类、售价、状态、更新时间。
- 表单：`name`, `categoryId`, `price`, `image`, `description`, `status`, `setmealDishes`(dishId, copies, name, price)。
- 依赖：从 `/admin/dish/list?cateId=` 拉取菜品供勾选，支持批量上下架、删除。

### 订单管理（OrderList）
- 统计卡片：待接单、待派送、派送中、已完成、已取消。
- 筛选：订单号、手机号、状态、下单时间范围。
- 表格列：订单号、客户、电话、金额、状态、下单时间、操作。
- 详情：显示地址、支付方式、菜品清单，提供接单/拒单/取消/派送/完成按钮。

### 工作台 & 报表
- 工作台：`BusinessDataVO`、`OrderOverViewVO`、`DishOverViewVO`、`SetmealOverViewVO` 数据卡。
- 报表：`TurnoverReportVO`, `UserReportVO`, `OrderReportVO` 生成折线图，`SalesTop10ReportVO` 生成柱状/饼图；导出按钮调用 `/admin/report/export`。

### 店铺设置 & 通用上传
- 店铺状态：`/admin/shop/status` GET/PUT，使用 `Switch` 控制营业/打烊，并展示最后更新时间。
- 上传：通用 `ImageUploader` 封装 `FormData` 请求，返回 URL 写入表单字段，支持菜品/套餐复用。
## Shared Infrastructure
- Centralized Axios instance with automatic token refresh handling (future).
- Custom hooks: `useAuth`, `usePagination`, `useRequest` for cleaner components.
- Reusable components for tables, forms, and modals (e.g., `DataTable`, `EntityForm`).

This plan drives upcoming implementation tasks.
