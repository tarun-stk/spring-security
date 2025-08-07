# Section-2

- Used JDBCUserDetailsManager to store user details in db
- Removes InMemoryUserDetailsManager, which stored user details in users map(in mry)

- Later added CustomUserDetailsService to use our own db scripts, and logic related to loading user details from our custom tables
- Removed JDBCUserDetailsManager bean
- Added /register api to add more customers in our db, who can also access all the secured apis
- Added logic related to disabling csrf, cause by default spring sec will secure all write operations.