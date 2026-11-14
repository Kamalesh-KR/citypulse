-- ============================================================
-- CITYPULSE — BENGALURU 50 ROADS + 25 LOCATIONS SEED
-- ============================================================
-- Idempotent: uses INSERT IGNORE and checks for existing road_code
-- Safe to run multiple times.
--
-- Your existing 5 roads (MGR001, BRG001, RES001, RCH001, RES002)
-- and 4 locations will NOT be touched.
-- ============================================================

USE citypulse_traffic_db;

-- --------------------------------------------------------
-- STEP 1: 25 Bengaluru locations
-- --------------------------------------------------------
INSERT IGNORE INTO locations
    (name, address, city, state, latitude, longitude,
     location_type, active, created_at, updated_at)
VALUES
    ('Majestic', 'Kempegowda Bus Station, Majestic', 'Bengaluru', 'Karnataka',
     12.9765, 77.5713, 'MAJOR_JUNCTION', true, NOW(), NOW()),

    ('Hebbal', 'Hebbal Flyover, Outer Ring Road', 'Bengaluru', 'Karnataka',
     13.0358, 77.5970, 'JUNCTION', true, NOW(), NOW()),

    ('Whitefield', 'Whitefield Main Road', 'Bengaluru', 'Karnataka',
     12.9698, 77.7499, 'TECH_HUB', true, NOW(), NOW()),

    ('Electronic City', 'Electronic City Phase 1', 'Bengaluru', 'Karnataka',
     12.8447, 77.6602, 'TECH_HUB', true, NOW(), NOW()),

    ('Silk Board', 'Central Silk Board Junction', 'Bengaluru', 'Karnataka',
     12.9172, 77.6229, 'JUNCTION', true, NOW(), NOW()),

    ('Marathahalli', 'Marathahalli Bridge', 'Bengaluru', 'Karnataka',
     12.9591, 77.6974, 'JUNCTION', true, NOW(), NOW()),

    ('Yeshwantpur', 'Yeshwantpur Railway Station', 'Bengaluru', 'Karnataka',
     13.0284, 77.5540, 'JUNCTION', true, NOW(), NOW()),

    ('Jayanagar', 'Jayanagar 4th Block', 'Bengaluru', 'Karnataka',
     12.9250, 77.5938, 'RESIDENTIAL', true, NOW(), NOW()),

    ('Indiranagar', 'Indiranagar 100ft Road', 'Bengaluru', 'Karnataka',
     12.9719, 77.6412, 'RESIDENTIAL', true, NOW(), NOW()),

    ('Koramangala', 'Koramangala 80ft Road', 'Bengaluru', 'Karnataka',
     12.9352, 77.6245, 'RESIDENTIAL', true, NOW(), NOW()),

    ('HSR Layout', 'HSR Layout 27th Main', 'Bengaluru', 'Karnataka',
     12.9121, 77.6446, 'RESIDENTIAL', true, NOW(), NOW()),

    ('BTM Layout', 'BTM Layout 16th Main', 'Bengaluru', 'Karnataka',
     12.9166, 77.6101, 'RESIDENTIAL', true, NOW(), NOW()),

    ('Rajajinagar', 'Rajajinagar Main Road', 'Bengaluru', 'Karnataka',
     12.9916, 77.5553, 'RESIDENTIAL', true, NOW(), NOW()),

    ('Banashankari', 'Banashankari 2nd Stage', 'Bengaluru', 'Karnataka',
     12.9255, 77.5468, 'RESIDENTIAL', true, NOW(), NOW()),

    ('JP Nagar', 'JP Nagar 3rd Phase', 'Bengaluru', 'Karnataka',
     12.9063, 77.5857, 'RESIDENTIAL', true, NOW(), NOW()),

    ('Vijayanagar', 'Vijayanagar Main Road', 'Bengaluru', 'Karnataka',
     12.9719, 77.5306, 'RESIDENTIAL', true, NOW(), NOW()),

    ('Yelahanka', 'Yelahanka Main Road', 'Bengaluru', 'Karnataka',
     13.1007, 77.5963, 'SUBURB', true, NOW(), NOW()),

    ('Devanahalli', 'Devanahalli Highway', 'Bengaluru', 'Karnataka',
     13.2437, 77.7138, 'SUBURB', true, NOW(), NOW()),

    ('Kengeri', 'Kengeri Main Road', 'Bengaluru', 'Karnataka',
     12.9178, 77.4839, 'SUBURB', true, NOW(), NOW()),

    ('RR Nagar', 'Rajarajeshwari Nagar', 'Bengaluru', 'Karnataka',
     12.9255, 77.5183, 'SUBURB', true, NOW(), NOW()),

    ('Bellandur', 'Bellandur Junction', 'Bengaluru', 'Karnataka',
     12.9304, 77.6784, 'TECH_HUB', true, NOW(), NOW()),

    ('Nelamangala', 'Nelamangala Toll Plaza', 'Bengaluru', 'Karnataka',
     13.0990, 77.3900, 'SUBURB', true, NOW(), NOW()),

    ('Anekal', 'Anekal Town', 'Bengaluru', 'Karnataka',
     12.7100, 77.6960, 'SUBURB', true, NOW(), NOW()),

    ('Hoskote', 'Hoskote Town', 'Bengaluru', 'Karnataka',
     13.0700, 77.7980, 'SUBURB', true, NOW(), NOW()),

    ('Bommasandra', 'Bommasandra Industrial Area', 'Bengaluru', 'Karnataka',
     12.8260, 77.6890, 'INDUSTRIAL', true, NOW(), NOW());

-- --------------------------------------------------------
-- STEP 2: 50 Bengaluru roads
-- --------------------------------------------------------
INSERT IGNORE INTO roads
    (name, road_code, start_location, end_location,
     length_km, lanes, speed_limit, active, created_at, updated_at)
VALUES
    ('Hebbal Flyover', 'BLR-HEB-FLY', 'Hebbal', 'Majestic',
     12.50, 6, 80, true, NOW(), NOW()),
    ('Outer Ring Road - East 1', 'BLR-ORR-E1', 'Marathahalli', 'Bellandur',
     4.50, 6, 60, true, NOW(), NOW()),
    ('Outer Ring Road - East 2', 'BLR-ORR-E2', 'Bellandur', 'Silk Board',
     6.80, 6, 60, true, NOW(), NOW()),
    ('Outer Ring Road - East 3', 'BLR-ORR-E3', 'Silk Board', 'HSR Layout',
     3.20, 6, 60, true, NOW(), NOW()),
    ('Outer Ring Road - North 1', 'BLR-ORR-N1', 'Hebbal', 'Yelahanka',
     8.50, 6, 80, true, NOW(), NOW()),
    ('Outer Ring Road - North 2', 'BLR-ORR-N2', 'Hebbal', 'Bellary Road Junction',
     4.20, 6, 80, true, NOW(), NOW()),
    ('Outer Ring Road - West 1', 'BLR-ORR-W1', 'RR Nagar', 'Kengeri',
     7.30, 6, 60, true, NOW(), NOW()),
    ('Outer Ring Road - West 2', 'BLR-ORR-W2', 'Kengeri', 'Nelamangala',
     9.80, 6, 60, true, NOW(), NOW()),
    ('Old Airport Road', 'BLR-OAR-01', 'Marathahalli', 'Koramangala',
     8.30, 6, 60, true, NOW(), NOW()),
    ('Hosur Road', 'BLR-HOS-01', 'Silk Board', 'Electronic City',
     9.20, 6, 80, true, NOW(), NOW()),
    ('Bellary Road', 'BLR-BEL-01', 'Hebbal', 'Devanahalli',
     22.00, 6, 80, true, NOW(), NOW()),
    ('Tumkur Road', 'BLR-TUM-01', 'Yeshwantpur', 'Nelamangala',
     15.50, 4, 80, true, NOW(), NOW()),
    ('Mysore Road', 'BLR-MYS-01', 'Majestic', 'Kengeri',
     11.00, 6, 60, true, NOW(), NOW()),
    ('Kanakapura Road', 'BLR-KAN-01', 'Banashankari', 'JP Nagar',
     8.50, 4, 60, true, NOW(), NOW()),
    ('NICE Road', 'BLR-NICE-01', 'Electronic City', 'RR Nagar',
     28.00, 6, 100, true, NOW(), NOW()),
    ('Whitefield Main Road', 'BLR-WHF-01', 'Marathahalli', 'Whitefield',
     12.30, 4, 60, true, NOW(), NOW()),
    ('Sarjapur Road', 'BLR-SAR-01', 'Koramangala', 'HSR Layout',
     9.80, 6, 60, true, NOW(), NOW()),
    ('Electronic City Flyover', 'BLR-ECF-01', 'Silk Board', 'Electronic City',
     6.50, 4, 60, true, NOW(), NOW()),
    ('Silk Board Flyover', 'BLR-SBF-01', 'BTM Layout', 'HSR Layout',
     2.80, 6, 60, true, NOW(), NOW()),
    ('Marathahalli Bridge', 'BLR-MTB-01', 'Marathahalli', 'Bellandur',
     3.20, 6, 60, true, NOW(), NOW()),
    ('KR Puram Bridge', 'BLR-KRP-01', 'Hoskote', 'Marathahalli',
     8.50, 4, 60, true, NOW(), NOW()),
    ('Jayanagar 4th Block Road', 'BLR-JAY-01', 'Jayanagar', 'JP Nagar',
     4.20, 2, 40, true, NOW(), NOW()),
    ('Majestic-KBS Road', 'BLR-MJT-01', 'Majestic', 'Yeshwantpur',
     5.80, 4, 40, true, NOW(), NOW()),
    ('Yeshwantpur Circle', 'BLR-YES-01', 'Yeshwantpur', 'Rajajinagar',
     3.50, 4, 40, true, NOW(), NOW()),
    ('Rajajinagar Main Road', 'BLR-RAJ-01', 'Rajajinagar', 'Vijayanagar',
     4.80, 4, 50, true, NOW(), NOW()),
    ('Indiranagar 100ft Road', 'BLR-IND-01', 'Indiranagar', 'Koramangala',
     5.20, 4, 40, true, NOW(), NOW()),
    ('Koramangala 80ft Road', 'BLR-KOR-01', 'Koramangala', 'BTM Layout',
     3.80, 4, 40, true, NOW(), NOW()),
    ('HSR 27th Main', 'BLR-HSR-01', 'HSR Layout', 'BTM Layout',
     3.50, 2, 40, true, NOW(), NOW()),
    ('BTM 16th Main', 'BLR-BTM-01', 'BTM Layout', 'Jayanagar',
     3.20, 2, 40, true, NOW(), NOW()),
    ('Banashankari 2nd Stage Road', 'BLR-BAN-01', 'Banashankari', 'JP Nagar',
     3.60, 4, 40, true, NOW(), NOW()),
    ('JP Nagar 3rd Phase Road', 'BLR-JPN-01', 'JP Nagar', 'BTM Layout',
     4.10, 4, 50, true, NOW(), NOW()),
    ('Vijayanagar Main Road', 'BLR-VIJ-01', 'Vijayanagar', 'RR Nagar',
     6.50, 4, 50, true, NOW(), NOW()),
    ('Yelahanka Main Road', 'BLR-YEL-01', 'Yelahanka', 'Hebbal',
     8.20, 4, 60, true, NOW(), NOW()),
    ('Devanahalli Highway', 'BLR-DEV-01', 'Devanahalli', 'Yelahanka',
     15.00, 6, 80, true, NOW(), NOW()),
    ('Kengeri Main Road', 'BLR-KEN-01', 'Kengeri', 'RR Nagar',
     7.80, 4, 60, true, NOW(), NOW()),
    ('RR Nagar Ring Road', 'BLR-RRN-01', 'RR Nagar', 'Vijayanagar',
     5.60, 4, 50, true, NOW(), NOW()),
    ('Bellandur-Anekal Road', 'BLR-BAN-02', 'Bellandur', 'Anekal',
     14.20, 4, 60, true, NOW(), NOW()),
    ('Bommasandra-Hoskote Road', 'BLR-BH-01', 'Bommasandra', 'Hoskote',
     12.80, 4, 60, true, NOW(), NOW()),
    ('Electronic City-Bommasandra', 'BLR-ECB-01', 'Electronic City', 'Bommasandra',
     6.40, 4, 60, true, NOW(), NOW()),
    ('Nelamangala-Tumkur Road', 'BLR-NEL-01', 'Nelamangala', 'Yeshwantpur',
     18.50, 4, 80, true, NOW(), NOW()),
    ('Anekal-Hosur Road', 'BLR-AKH-01', 'Anekal', 'Bommasandra',
     9.80, 4, 60, true, NOW(), NOW()),
    ('Hoskote-KR Puram', 'BLR-HKP-01', 'Hoskote', 'KR Puram Bridge',
     11.20, 4, 60, true, NOW(), NOW()),
    ('Whitefield-Hoskote Road', 'BLR-WHK-01', 'Whitefield', 'Hoskote',
     10.50, 4, 60, true, NOW(), NOW()),
    ('Sarjapur-Whitefield Link', 'BLR-SWL-01', 'HSR Layout', 'Whitefield',
     11.80, 4, 60, true, NOW(), NOW()),
    ('Bellandur-Sarjapur Link', 'BLR-BSL-01', 'Bellandur', 'Sarjapur Road',
     5.20, 4, 60, true, NOW(), NOW()),
    ('Brigade Road Extension', 'BLR-BRG-02', 'Brigade Road Junction', 'Richmond Circle',
     1.40, 3, 40, true, NOW(), NOW()),
    ('MG Road Extension', 'BLR-MG-02', 'MG Road Junction', 'Trinity Circle',
     1.60, 4, 40, true, NOW(), NOW()),
    ('Cunningham Road', 'BLR-CUN-01', 'MG Road Junction', 'Rajajinagar',
     4.80, 4, 50, true, NOW(), NOW()),
    ('Palace Road', 'BLR-PAL-01', 'Majestic', 'Jayanagar',
     5.20, 4, 50, true, NOW(), NOW()),
    ('Race Course Road', 'BLR-RCR-01', 'Majestic', 'Yeshwantpur',
     4.50, 4, 50, true, NOW(), NOW()),
    ('Kasturba Road', 'BLR-KAS-01', 'MG Road Junction', 'Richmond Circle',
     1.80, 2, 40, true, NOW(), NOW());

-- --------------------------------------------------------
-- STEP 3: VERIFY
-- --------------------------------------------------------
SELECT 'Locations in Bengaluru' AS metric, COUNT(*) AS cnt
FROM locations WHERE city = 'Bengaluru';

SELECT 'Roads total' AS metric, COUNT(*) AS cnt FROM roads;

SELECT 'New BLR roads' AS metric, COUNT(*) AS cnt
FROM roads WHERE road_code LIKE 'BLR-%';