FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:1.27-alpine

RUN addgroup -S angular && adduser -S angular -G angular

COPY --from=build /app/dist/financial-platform-ui/browser/ /usr/share/nginx/html/
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

ENV API_UPSTREAM=http://api-gateway:8080

RUN chown -R angular:angular /usr/share/nginx/html

USER angular

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q -O - http://localhost:80/ || exit 1

ENTRYPOINT ["/bin/sh", "-c", "envsubst \"$API_UPSTREAM\" < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g \"daemon off;\""]
