#!/bin/bash

# Redis 세션 관련 테스트
# 주의: user.sh를 먼저 실행하여 testuser가 생성되어 있어야 합니다.

BASE_URL="http://localhost:8080"
COOKIE_JAR="session_cookies.txt"

# Function to make curl request with HTTP status code
do_curl() {
  curl -s -w "\nHTTP Status: %{http_code}\n" "$@"
}

rm -f "$COOKIE_JAR"

echo "=== Redis Session Test ==="
echo ""

echo "=== 1. Login (testuser) ==="
do_curl -X POST "$BASE_URL/api/v1/login" \
  -c "$COOKIE_JAR" \
  -d "username=testuser&password=password123"
echo

echo ""
echo "=== 2. Show session cookie ==="
cat "$COOKIE_JAR"
echo ""

echo ""
echo "=== 3. Access authenticated API with session ==="
do_curl -X GET "$BASE_URL/api/v1/users/me" \
  -b "$COOKIE_JAR"
echo

echo ""
echo "=== 4. Check Redis for session keys ==="
docker exec snsredis redis-cli keys '*'

echo ""
echo "=== 5. Logout ==="
do_curl -X POST "$BASE_URL/api/v1/logout" \
  -b "$COOKIE_JAR" \
  -c "$COOKIE_JAR"
echo

echo ""
echo "=== 6. Access after logout (should fail 401) ==="
do_curl -X GET "$BASE_URL/api/v1/users/me" \
  -b "$COOKIE_JAR"
echo

echo ""
echo "=== 7. Check Redis after logout (session should be removed) ==="
docker exec snsredis redis-cli keys '*'

rm -f "$COOKIE_JAR"
