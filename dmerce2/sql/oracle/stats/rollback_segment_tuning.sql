-- #############################################################################################
--
-- %Purpose: Guide for Tuning the Rollback Segments
--
-- #############################################################################################

1). Size and Number of Waits per Rollback-Segment
-------------------------------------------------
SELECT SUBSTR(rsn.name,1,10) "Name",
       rss.rssize "Tot-Size [Bytes]",
       rss.extents "Extents",
       ROUND(rss.rssize/rss.extents) "RS-Size [Bytes]",
       waits "Number Waits"
  FROM v$rollstat rss, v$rollname rsn
 WHERE rss.usn = rsn.usn
 ORDER BY rsn.name;

Old Tuning Session: (Rollback Segments with 400 KB Size)

Name       Tot-Size [Bytes]    Extents RS-Size [Bytes] Number Waits
---------- ---------------- ---------- --------------- ------------
RBS01               8597504         21          409405         1616
RBS02               8597504         21          409405         4125
RBS03               8597504         21          409405         3992
RBS04               8597504         21          409405         4174
RBS05               8597504         21          409405         3617
RBS06               8597504         21          409405         3843
RBS07               8597504         21          409405         3715
RBS08               8597504         21          409405         3730
RBS09               8597504         21          409405        25699
RBS10               8597504         21          409405         3635

New Tuning Session: (Rollback Segments with 5 MB Size)

Name       Tot-Size [Bytes]    Extents RS-Size [Bytes] Number Waits
---------- ---------------- ---------- --------------- ------------
RBS01             104853504         20         5242675         1715
RBS02             104853504         20         5242675         1835
RBS03             104853504         20         5242675         1338
RBS04             104853504         20         5242675         1499
RBS05             104853504         20         5242675         1572
RBS06             104853504         20         5242675         1628
RBS07             104853504         20         5242675         1533
RBS08             104853504         20         5242675         1689
RBS09             104853504         20         5242675         1461
RBS10             104853504         20         5242675         1663

2). Rollback Contention
-----------------------
SELECT name,gets,waits,
       to_char(((gets-waits)*100)/gets,'999.9999') hit_ratio
  FROM v$rollstat S, v$rollname R
 WHERE S.usn = R.usn
ORDER BY R.name;

Old Tuning Session: (Rollback Segments with 400 KB Size)

NAME                                 GETS      WAITS HIT_RATIO
------------------------------ ---------- ---------- ---------
RBS01                             5314092       1643   99.9691
RBS02                            10363748       4157   99.9599
RBS03                            10459920       4017   99.9616
RBS04                            10962299       4184   99.9618
RBS05                             9469712       3649   99.9615
RBS06                            10218019       3889   99.9619
RBS07                             9796463       3736   99.9619
RBS08                             9900727       3739   99.9622
RBS09                            13130819      25721   99.8041
RBS10                             9456272       3673   99.9612

New Tuning Session: (Rollback Segments with 5 MB Size)

NAME                                 GETS      WAITS HIT_RATIO
------------------------------ ---------- ---------- ---------
RBS01                             5837671       1719   99.9706
RBS02                             6151758       1835   99.9702
RBS03                             5451355       1338   99.9755
RBS04                             5105157       1499   99.9706
RBS05                             5333881       1574   99.9705
RBS06                             6070279       1631   99.9731
RBS07                             5611779       1533   99.9727
RBS08                             6097782       1692   99.9723
RBS09                             5558601       1462   99.9737
RBS10                             6418860       1663   99.9741

3). Compare rollback segment waits with total number of gets
------------------------------------------------------------
Rollback segment waits

SELECT v.class, v.count
  FROM v$waitstat v
 WHERE class IN ('system undo header','system undo block',
                      'undo header','undo block');

Total number of gets

SELECT TO_CHAR(sum(value),'999,999,999,999') "Total Gets"
  FROM v$sysstat
 WHERE name in ('db block gets','consistent gets');

Old Tuning Session: (Much waits compared with total Gets)

CLASS                   COUNT
------------------ ----------
system undo header          0
system undo block           0
undo header             74880
undo block              21618

Total Gets
----------------
   8,896,140,872

New Tuning Session: (Much waits compared with total Gets, not better)

CLASS                   COUNT
------------------ ----------
system undo header          0
system undo block           0
undo header             24138
undo block              28146

Total Gets
----------------
   4,152,462,130


4). Overall System-Statistics for ALL Rollback-Segments
-------------------------------------------------------
SELECT statistic#,SUBSTR(name,1,50) "Name",
       class,value
  FROM v$sysstat
  WHERE name in ('user rollbacks',
                 'rollback changes - undo records applied',
                 'transaction rollbacks');

Old Tuning Session

STATISTIC# Name                                                    CLASS        VALUE
---------- -------------------------------------------------- ---------- ------------
         5 user rollbacks                                              1           30
       113 rollback changes - undo records applied                   128    2'524'864
       114 transaction rollbacks                                     128       29'131

A low number of rollbacks initiate a high nunber of waits !

New Tuning Session

STATISTIC# Name                                                    CLASS      VALUE
---------- -------------------------------------------------- ---------- ----------
         5 user rollbacks                                              1         51
       113 rollback changes - undo records applied                   128     111886
       114 transaction rollbacks                                     128      13649

5). Who uses the Rollback Segments
----------------------------------
SELECT r.usn,SUBSTR(r.name,1,10) "Name",s.osuser,
       SUBSTR(s.username,1,15) "User",s.sid,x.extents,
       x.extends,x.waits,x.shrinks,
       x.wraps
FROM   sys.v_$rollstat X,
       sys.v_$rollname R,
       sys.v_$session S,
       sys.v_$transaction T
WHERE  t.addr       = s.taddr (+)
AND    x.usn (+)    = r.usn
AND    t.xidusn (+) = r.usn
ORDER BY r.usn;

Old Tuning Session

       USN Name       OSUSER          User                   SID    EXTENTS    EXTENDS      WAITS    SHRINKS      WRAPS
---------- ---------- --------------- --------------- ---------- ---------- ---------- ---------- ---------- ----------
         0 SYSTEM                                                         4          0          0          0          0
         2 RBS01      poseidon        VP_LINK                 55         21        321       1703         34       3448
         3 RBS02      ota             DISPATCHER              35         21        370       4175         42       6589
         4 RBS03      poseidon        VP_LINK                 59         21        445       4074         53       6981
         5 RBS04      poseidon        VP_LINK                 56         21        262       4252         33       7073
         6 RBS05      bscs            SMH_LINK                76         21        195       3772         22       5847
         7 RBS06                                                         21        291       4044         34       6665
         8 RBS07                                                         21        259       3803         31       6336
         9 RBS08      poseidon        VP_LINK                 57         21        529       3820         64       6789
        10 RBS09      poseidon        VP_LINK                 54         21        599      25839         69       8153
        11 RBS10                                                         21        398       3772         45       5878

New Tuning Session

       USN Name       OSUSER          User                   SID    EXTENTS    EXTENDS      WAITS    SHRINKS      WRAPS
---------- ---------- --------------- --------------- ---------- ---------- ---------- ---------- ---------- ----------
         0 SYSTEM                                                         4          0          0          0          1
         1 RBS10                                                         20          0       1681          0        299
         2 RBS01                                                         20          0       1731          0        277
         3 RBS02      poseidon        VP_LINK                 93         20          0       1850          0        288
         4 RBS03      poseidon        VP_LINK                 92         20          0       1338          0        248
         5 RBS04      poseidon        VP_LINK                 86         20          0       1499          0        232
         6 RBS05      bscs            SMH_LINK                94         20          0       1583          0        258
         7 RBS06      bscs            SMH_LINK               113         20          0       1645          0        282
         8 RBS07      poseidon        VP_LINK                 70         20          0       1533          0        260
         9 RBS08                                                         20          0       1694          0        274
        10 RBS09                                                         20          0       1483          0        264

No more extends and shrinks.
