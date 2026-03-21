#!/bin/bash

# 인증 관련 API 테스트 (로그인, 로그아웃)
# 주의: user.sh를 먼저 실행하여 testuser가 생성되어 있어야 합니다.

BASE_URL="http://localhost:8080"
COOKIE_JAR="cookies.txt"

# Function to make curl request with HTTP status code
do_curl() {
  curl -s -w "\nHTTP Status: %{http_code}\n" "$@"
}

rm -f "$COOKIE_JAR"

echo "=== 1. Login (testuser) ==="
do_curl -X POST "$BASE_URL/api/v1/login" \
  -c "$COOKIE_JAR" \
  -d "username=testuser&password=password123"
echo

echo ""
echo "=== 2. Login Failed (wrong password - should fail 401) ==="
do_curl -X POST "$BASE_URL/api/v1/login" \
  -d "username=testuser&password=wrongpassword"
echo

echo ""
echo "=== 3. Login Failed (unknown user - should fail 401) ==="
do_curl -X POST "$BASE_URL/api/v1/login" \
  -d "username=unknownuser&password=password123"
echo

echo ""
echo "=== 4. Access authenticated API (with session) ==="
do_curl -X GET "$BASE_URL/api/v1/users/me" \
  -b "$COOKIE_JAR"
echo

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

rm -f "$COOKIE_JAR"
