# Moment Client

This is the React frontend for the Moment Takeout application.

## Setup

1.  Install dependencies:
    ```bash
    npm install
    ```

2.  Start the development server:
    ```bash
    npm start
    ```

## Features

-   **Admin Portal**
    -   Login (`/admin/login`) with token persistence
    -   Employee & Category management (pagination, validation, status toggles)
    -   Dish & Setmeal management with flavor binding, OSS image upload, and batch ops
    -   Order center featuring advanced filtering, detail drawer, and operational workflows
    -   Dashboard with turnover/user/order trends plus sales top 10 (powered by `/admin/report/*`)
    -   Settings panel for shop status control, OSS sanity check, and workspace snapshots
-   **User Portal**
    -   Login (`/user/login`) – mock WeChat auth
    -   Menu browsing per category (dishes + setmeals)
    -   Shopping cart, checkout, and historical orders

## Configuration

-   The API proxy defaults to `http://localhost:8080`. Adjust the `proxy` field in `package.json` or create a `.env` file with `REACT_APP_API_BASE` if you deploy elsewhere.
-   Admin/user JWT tokens are stored under `adminToken` / `userToken` in `localStorage`.
-   OSS uploads rely on the backend `/admin/common/upload` endpoint; verify bucket credentials there.

## QA Checklist

See `docs/QA-checklist.md` for recommended manual verification steps that cover admin and user flows end-to-end.
