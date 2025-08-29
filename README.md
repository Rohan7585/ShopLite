# 🛒 Project: “ShopLite” (JWT + Spring Security)

## Roles

* `ROLE_ADMIN`
* `ROLE_CUSTOMER`

## Core Domain

* **User**(id, username, password, role, active)
* **Product**(id, name, price, stock, active)
* **Order**(id, customerId, items\[], status\[PENDING/PAID/SHIPPED], total)
* **OrderItem**(productId, qty, priceSnapshot)

## Public endpoints (no auth)

* `GET /api/public/products` – list active products
* `GET /api/public/products/{id}` – product details
* `POST /api/auth/signup` – create customer account
* `POST /api/auth/login` – returns JWT

## Admin endpoints (JWT, `ROLE_ADMIN`)

* `POST /api/admin/products` – create product
* `PUT /api/admin/products/{id}` – update product/stock/active
* `GET /api/admin/orders` – list all orders (filter by status)
* `PUT /api/admin/orders/{id}/status` – update order status
* `PUT /api/admin/users/{id}/deactivate` – soft-deactivate a user

## Customer endpoints (JWT, `ROLE_CUSTOMER`)

* `GET /api/customer/me` – my profile
* `POST /api/customer/cart/checkout` – place order from cart
* `GET /api/customer/orders` – my orders
* `GET /api/customer/orders/{id}` – view my one order (owner-only)

## Authorization rules (at a glance)

* `/api/admin/**` → `hasRole('ADMIN')`
* `/api/customer/**` → `hasRole('CUSTOMER')`
* Owner checks: `@PreAuthorize("#customerId == authentication.name or hasRole('ADMIN')")` where relevant (e.g., fetching an order by id).

## JWT flow (stateless)

1. `POST /api/auth/login` → validate credentials → issue JWT (roles in claims).
2. Client sends `Authorization: Bearer <token>` on every call.
3. Server validates token in a JWT filter → builds `Authentication` → applies URL/method rules.
