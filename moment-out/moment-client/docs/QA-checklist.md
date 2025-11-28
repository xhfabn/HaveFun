# Moment Client QA Checklist

This guide captures the core manual verification steps to keep the admin and user portals aligned with backend capabilities.

## Environment Prep

1. Backend services (gateway, moment-out) running locally with seed data imported.
2. `npm install` executed for the React client (`@ant-design/plots` required for dashboard).
3. Browser cache cleared between admin/user sessions or use separate profiles.

## Admin Portal

### Authentication
- Login with a known employee account. Verify invalid credentials show server error message.
- Refresh after login to ensure `adminToken` persists and guarded routes remain accessible.

### Dashboard & Reports
- Visit `/admin/dashboard`, select “近7天/近30天”, confirm charts update and loading spinner disappears.
- Click “查看订单” to navigate to the Orders page.

### Employee Management
- Create a new employee, ensure form validations trigger for missing phone/username.
- Toggle status and confirm list refresh; attempt login using disabled account to ensure backend rejection (optional).

### Category Management
- Filter by type/name, edit existing category, and verify ordering numbers accept integers only.
- Delete a category that is unused to confirm success; expect backend error when linked to dishes.

### Dish Management
- Add dish with flavors and image upload; confirm OSS URL is auto-filled.
- Edit dish to ensure flavor data pre-populates (comma-separated values restored correctly).
- Batch delete multiple draft dishes and observe success toast.

### Setmeal Management
- Create setmeal by selecting several dishes; verify auto-filled price/copies logic.
- Change setmeal status via switch; confirm list refresh and backend reflects new status.

### Orders Page
- Filter by phone/date range; ensure pagination updates.
- Use action buttons: 接单 → 派送 → 完成 flow and capture success toasts.
- Trigger 拒单/取消 to confirm reason modal validation.
- Open detail drawer and check dish breakdown matches backend order.

### Settings (Common)
- Toggle shop status switch and ensure toast + state update.
- Upload a sample image using OSS tester; copy resulting URL to ensure CDN path accessible.
- Review business/overview cards for non-null values; if backend lacks data, document discrepancy.

## User Portal

### Authentication
- Login via `/user/login`, confirm token stored as `userToken`.

### Menu & Cart
- Switch categories; dishes vs. setmeals should render appropriately.
- Add dish that requires flavor selection; ensure modal enforces full selection before adding to cart.
- Add setmeal items, verify cart counts/amount totals update and survive refresh.

### Checkout
- Open checkout drawer only when cart non-empty; ensure address dropdown defaults correctly.
- Submit order and capture backend-generated order number from toast.

### Orders & Addresses
- Check `/user/orders` for newly submitted order; statuses should reflect server values.
- Update address data and confirm list refresh.

## Regression Notes
- After each admin action, revisit relevant user views (e.g., new dishes appear on menu, shop status affects ordering if backend enforces it).
- Capture screenshots or console logs for anomalies and record them in issue tracker.

Use this document as a living checklist—annotate outcomes per release to streamline future QA passes.
