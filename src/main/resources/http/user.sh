#!/bin/bash

# Base Auth API Test (Signup, Login, Me)

BASE_URL="http://localhost:8080"
COOKIE_JAR="cookies.txt"

do_curl() {
  curl -s -w "\nHTTP Status: %{http_code}\n" "$@"
}

rm -f "$COOKIE_JAR"

echo "=== 1. Sign Up (testuser) ==="
do_curl -X POST "$BASE_URL/api/v1/users/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
echo

echo "=== 2. Sign Up (testuser2) ==="
do_curl -X POST "$BASE_URL/api/v1/users/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser2",
    "password": "password456"
  }'
echo

echo "=== 3. Sign Up Duplicate (Should Fail 400) ==="
do_curl -X POST "$BASE_URL/api/v1/users/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
echo

echo "=== 4. Login (testuser) ==="
do_curl -X POST "$BASE_URL/api/v1/login" \
  -c "$COOKIE_JAR" \
  -d "username=testuser&password=password123"
echo

echo "=== 5. Get My Info (Authenticated) ==="
do_curl -X GET "$BASE_URL/api/v1/users/me" \
  -b "$COOKIE_JAR"
echo

echo "=== 6. Get My Info (Without Login - Should Fail 401) ==="
do_curl -X GET "$BASE_URL/api/v1/users/me"
echo

rm -f "$COOKIE_JAR"
