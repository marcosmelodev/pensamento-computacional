# Lista — udfilasystem-backend

## Estrutura do Projeto

- udfilasystem-backend/
  - package.json
  - package-lock.json
  - .env
  - .gitignore
  - README.md
  - src/
    - server.js
    - app.js
    - config/
      - database.js
      - auth.js
    - routes/
      - index.js
      - userRoutes.js
      - queueRoutes.js
      - ticketRoutes.js
    - controllers/
      - userController.js
      - queueController.js
      - ticketController.js
    - models/
      - User.js
      - Queue.js
      - Ticket.js
    - middlewares/
      - authMiddleware.js
      - errorMiddleware.js
    - services/
      - userService.js
      - queueService.js
      - ticketService.js
    - utils/
      - generateToken.js
      - hashPassword.js
