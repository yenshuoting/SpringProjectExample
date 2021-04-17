

# 使用Spring系列打造小型專案

----

[TOC]

----

## 講師介紹

- 姓名 : 林泰永 (Ted)
- 學歷 : 朝陽科技大學 資訊工程系
- 現職 : 聖森雲端科技股份有限公司 技術主管
- 做過專案 :
  - 勞保局系統重構案、銀行財富管理系統、
    政府公文內部管理系統、醫院病歷系統...等等
- 擅長技術 : Java

----

## 第六章. 應用程式的安全管理 - Spring Security

### 1. Spring Security介紹

Spring Security是一個安全框架，提供驗證（authentication）與授權（authorization）等有關安全管理的功能。

**驗證（authentication）**是當伺服器被存取時，確認對方的身分。

**授權（authorization）**則是在身份確認後，判斷是否要准許對方的請求。

----

### 2. 加入Spring Security

#### 加入依賴 

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>sprixmng-boot-starter-security</artifactId>
</dependency>
```



#### Spring Security 設定檔

新增一個`SecurityConfig.java` 檔案繼承WebSecurityConfigurerAdapter來設定，

覆寫`configure()` 的方法設置http security 的參數

```java
@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests() // 定義哪些url需要被保護
            .antMatchers("/").permitAll()  // 定義匹配到"/" 不需要驗證
            .antMatchers("/swagger-ui.html").permitAll() // 匹配到"/swagger-ui.html", 不需要身份驗證
            .anyRequest().authenticated() // 其他尚未匹配到的url都需要身份驗證
            .and()
            .formLogin()
            .and()
            .httpBasic();
    }
}
```



設定一些角色

角色ADMIN 可以訪問/demo/user 與 /demo/admin

角色USER 只可以訪問/demo/user

```
@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        // 設置角色定義
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("123456")
                .roles("ADMIN", "USER") // 擁有ADMIN 與 USER角色
                .and()
                .withUser("user")
                .password("123")
                .roles("USER");// 擁有USER角色
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests() // 定義哪些url需要被保護
                .antMatchers("/user").hasRole("USER")// 定義匹配到"/user"底下的需要有USER的這個角色才能進去
                .antMatchers("/admin").hasRole("ADMIN") // 定義匹配到"/admin"底下的需要有ADMIN的這個角色才能進去
                .anyRequest().authenticated() // 其他尚未匹配到的url都需要身份驗證
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }
```

----

### 3. 使用 JWT

----

#### 加入依賴

```
<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt</artifactId>
			<version>0.9.1</version>
</dependency>
```

#### 創造JwtTokenUtils

```java
@Component
public class JwtToken implements Serializable {

    private static final long EXPIRATION_TIME = 1 * 60 * 1000;
    /**
     * JWT SECRET KEY
     */
    private static final String SECRET = "learn to dance in the rain";

    /**
     * 簽發JWT
     */
    public String generateToken(HashMap<String, String> userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put( "userName", userDetails.get("userName") );

        return Jwts.builder()
                .setClaims( claims )
                .setExpiration( new Date( Instant.now().toEpochMilli() + EXPIRATION_TIME  ) )
                .signWith( SignatureAlgorithm.HS512, SECRET )
                .compact();
    }

    /**
     * 驗證JWT
     */
    public void validateToken(String token) throws AuthException {
        try {
            Jwts.parser()
                    .setSigningKey( SECRET )
                    .parseClaimsJws( token );
        } catch (SignatureException e) {
            throw new AuthException("Invalid JWT signature.");
        }
        catch (MalformedJwtException e) {
            throw new AuthException("Invalid JWT token.");
        }
        catch (ExpiredJwtException e) {
            throw new AuthException("Expired JWT token");
        }
        catch (UnsupportedJwtException e) {
            throw new AuthException("Unsupported JWT token");
        }
        catch (IllegalArgumentException e) {
            throw new AuthException("JWT token compact of handler are invalid");
        }
    }
}
```

#### 建立登入

```java
@RestController
@RequestMapping("/api")
public class TodoController {

@PostMapping("/login")
public ResponseEntity login(@RequestBody HashMap <String, String> user) {
    JwtToken jwtToken = new JwtToken();
    String token = jwtToken.generateToken(user); // 取得token

    return ResponseEntity.status(HttpStatus.OK).body(token);
   }
}
```

----

### 4. Swagger上加入JWT認證

```java
				.securitySchemes(Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER
						.name("JWT")
						.build()))
				.securityContexts(Collections.singletonList(SecurityContext.builder()
						.securityReferences(Collections.singletonList(SecurityReference.builder()
								.scopes(new AuthorizationScope[0])
								.reference("JWT")
								.build()))
						.operationSelector(o -> o.requestMappingPattern().matches("/.*"))
						.build()))
```

----

### 5. JWT介紹

#### JWT介紹

1. JWT是JSON Web Token的簡寫，是一種開放標準(**RFC 7519**)，也就是基於JSON object的編碼，並透過這個編碼進行傳遞資訊。
2. JWT會透過HMAC、RSA、ECDS等演算法進行加密
3. 通常利用JWT來對使用者進行驗證，也就是使用者會先請求身分提供的伺服器給予該JWT，而後，只要使用者帶著這個JWT向資源伺服器請求資源，如果這個JWT是有效的，那麼就能獲取資源

----

#### JWT的組成

JWT的組合可以看成是三個JSON object，並且用**.**來做區隔，而這三個部分會各自進行編碼，組成一個JWT字串。

也就是變成：**Header.Payload.Signature**

##### Header

由兩個欄位組合：

1. alg

   也就是token被加密的演算法，如**HMAC**、**SHA256**、**RSA**

2. typ

   也就是token的type，基本上就是**JWT**

範例：

```
{
    "alg": "HS256",
    "typ": "JWT"
}
```

然後進行Base64進行編碼。Base64是透過64個字符來表示二進制數據的一種方法，編碼的方式是固定的而且是可以逆向解碼的，並不是那種安全的加密演算法。

##### Payload

這裡放的是聲明(Claim)內容，也就是用來放傳遞訊息的地方，在定義上有三種聲明：

1. Registered claims

可以想成是標準公認的一些訊息**建議**你可以放，但並不強迫，例如：

- iss(Issuer)：JWT簽發者
- exp(Expiration Time)：JWT的過期時間，過期時間必須大於簽發JWT時間
- sub(Subject)：JWT所面向的用戶
- aud(Audience)：接收JWT的一方
- nbf(Not Before)：也就是定義擬發放JWT之後，的某段時間點前該JWT仍舊是不可用的
- iat(Issued At)：JWT簽發時間
- jti(JWT Id)：JWT的身分標示，每個JWT的Id都應該是不重複的，避免重複發放

2. Public claims

這個，可以想成是傳遞的欄位必須是跟上面Registered claims欄位不能衝突，然後可以向官方申請定義公開聲明，會進行審核等步驟，實務上在開發上是不太會用這部分的。

3. Private claims

這個就是發放JWT伺服器可以自定義的欄位的部分，例如實務上會放User Account、User Name、User Role等**不敏感**的數據。

所謂不敏感的數據就是不會放使用者的密碼等敏感數據，因為該Payload傳遞的訊息最後也是透過Base64進行編碼，所以是可以被破解的，因此放使用者密碼會有安全性的問題。

##### Signature

由三大部分組成：

- base64UrlEncode(header)
- base64UrlEncode(payload)
- secret

也就是：

```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

header跟payload中間用**.**來串接，secret是存放在伺服器端的秘密字串，最後將這三個部分串接再一起的字串進行加密演算法進行加密。

----

#### 客戶端如何用JWT來訪問資源？

1. 前端會先透過存取後端的登入API，後端驗證使用者帳密成功後，就會發放合法JWT字串
2. 前端拿到JWT字串就會將JWT存放在Local Storage裡面
3. 而後當前端要存取受保護的資源API時，只要在Header的填寫以下內容：

```
Authorization: Bearer <JWT token>
```

4. 後端收到後，會去檢查Authorization的JWT token是否有效，如果有效，則允許前端訪問受保護的資源。在以前的Session的設計上，Session會存放在Redis等這種快取資料庫，每當使用者訪問受保護的資源時，會先去存取資料庫的Session進行比對，有效則讓使用者存取，以JWT的方式可以降低查詢資料庫的需求。

----

#### JWT的優缺點

##### 優點：

- 採用JSON object的形式，大部分的程式語言皆支援
- 可存放一些使用者資訊，但並非是敏感的資訊
- 整個JWT，只要Payload不要放過多的資訊，其實Size是相當小的
- 不用在Server的資料庫存放Session，特別適合多台Server的情境下，使得擴展性容易，因為多台Server要使用Session的話，會有共享Session的問題產生，
- 對於現在手機上的APP的應用特別好，使用者不用每次打開APP都要重新輸入帳號與密碼
- 支持跨域請求，不會有傳統用Cookie進行跨域請求等問題

##### 缺點：

- JWT沒辦法主動被中止，也就是說不能像Session一樣被強制無效，但是個人覺得這有很多方式可以避免
- JWT一旦洩漏會有很大的安全性的問題，但是洩漏通常會透過兩種方式：

1. 駭客使用你的電腦，並得知JWT

2. 使用中間人攻擊的方式，擷取客戶端傳送伺服器端的封包，並獲取JWT，但使用HTTPS傳輸可以大幅度降低該攻擊，只要定期更換SSL證書就可以了

----



## 第七章. 實做小型專案 - 活動管理系統資料庫模型

#### 資料庫模型

##### Member

| 欄位名  | 型別   | 說明                    |
| ------- | ------ | ----------------------- |
| ID      | Long   | PK                      |
| usrName | String | 使用者名稱              |
| usrPwd  | String | 使用者密碼              |
| eMail   | String | 使用者Email             |
| Role    | String | 使用者權限 : ADMIN,USER |

##### Events

| 欄位名       | 型別   | 說明       |
| ------------ | ------ | ---------- |
| ID           | Long   | PK         |
| eventName    | String | 活動名稱   |
| eventDate    | Date   | 活動日     |
| eventReqDate | Date   | 活動報名日 |
| eventStatus  | Int(3) | 活動狀態   |

##### UserEventReg

| 欄位名  | 型別 | 說明       |
| ------- | ---- | ---------- |
| userID  | Long | PK         |
| eventID | Long | PK         |
| reqDate | Date | 活動登錄日 |



### 1. 管理者端 - 活動編輯、活動查詢

##### 活動查詢

1. 列出活動列表

##### 活動編輯項目

1. 需管理者權限登錄
2. 有新增活動
3. 有修改活動
4. 有刪除活動

### 2. 使用者端 - 活動列表、活動報名

##### 活動列表

1.查詢出狀態為開放報名的列表

##### 活動報名

1. 加入報名
   1. 判斷無報名資料
   2. 加入一筆報名資料
   3. 回傳成功



----

## 第八章. Spring AOP

### AOP

#### AOP 觀念與術語

AOP全名為Aspect-Oriented Programming，有關於AOP的許多名詞術語都過於抽象，單從字面上並不容易理解其名詞意義，這邊將以之前介紹代理機制的範例來逐一對照以介紹AOP的術語與觀念：

##### Cross-cutting concern

在DynamicProxyDemo專案的例子中，記錄的 動作原先被橫切（Cross-cutting）入至HelloSpeaker本身所負責的商務流程之中，另外類似於日誌這類的動作，如安全 （Security）檢查、交易（Transaction）等系統層面的服務（Service），在一些應用程式之中常被見到安插至各個物件的處理流程之 中，這些動作在AOP的術語中被稱之為Cross-cutting concerns。

以圖片說明可強調出Cross-cutting concerns的意涵，例如原來的商務流程是很單純的：

![img](https://openhome.cc/Gossip/SpringGossip/images/AOPConcept-1.JPG)



Cross-cutting concerns若直接撰寫在負責某商務的物件之流程中，會使得維護程式的成本增高，例如若您今天要將物件中的記錄功能修改或是移除該服務，則必須修改所 有撰寫曾記錄服務的程式碼，然後重新編譯，另一方面，Cross-cutting concerns混雜於商務邏輯之中，使得商務物件本身的邏輯或程式的撰寫更為複雜。

現在為了要加入日誌（Logging）與安全（Security）檢查等服務，物件的程式碼中若被硬生生的寫入相關的Logging、Security程式片段，則可使用以下圖解表示出Cross-cutting與Cross-cutting concerns的概念：

![img](https://openhome.cc/Gossip/SpringGossip/images/AOPConcept-2.JPG)



Cross-cutting concerns若直接撰寫在負責某商務的物件之流程中，會使得維護程式的成本增高，例如若您今天要將物件中的日誌功能修改或是移除該服務，則必須修改所 有撰寫曾日誌服務的程式碼，然後重新編譯，另一方面，Cross-cutting concerns混雜於商務邏輯之中，使得商務物件本身的邏輯或程式的撰寫更為複雜。

##### Aspect

將散落於各個商務物件之中的Cross-cutting concerns收集起來，設計各個獨立可重用的物件，這些物件稱之為Aspect，例如在 [動態代理](https://openhome.cc/Gossip/SpringGossip/DynamicProxy.html) 中 將日誌的動作設計為一個LogHandler類別，LogHandler類別在AOP的術語就是Aspect的一個具體實例，在AOP中著重於 Aspect的辨認，將之從商務流程中獨立出來，在需要該服務的時候，縫合（Weave）至應用程式之上，不需要服務的時候，也可以馬上從應用程式中脫 離，應用程式中的可重用元件不用作任何的修改，例如在 [動態代理](https://openhome.cc/Gossip/SpringGossip/DynamicProxy.html) 中的HelloSpeaker所代表的角色就是應用程式中可重用的元件，在它需要日誌服務時並不用修改本身的程式碼。

另一方面，對於應用程式中可重用的元件來說，以AOP的設 計方式，它不用知道處理提供服務的物件之存在，具體的說，與服務相關的API不會出現在可重用的應用程式元件之中，因而可提高這些元件的重用性，您可以將 這些元件應用至其它的應用程式之中，而不會因為目前加入了某些服務而與目前的應用程式框架發生耦合。

##### Advice

Aspect的具體實作稱之為Advice，以日誌的動作而言，Advice中會包括真正的日誌程式碼是如何實作的，像是[動態代理](https://openhome.cc/Gossip/SpringGossip/DynamicProxy.html) 中的LogHandler類別就是Advice的一個具體實例，Advice中包括了Cross-cutting concerns的行為或所要提供的服務。

##### Joinpoint

Aspect在應用程式執行時加入商務流程的點或時機稱之為Joinpoint，具體來說，就是Advice在應用程式中被呼叫執行的時機，這個時機可能是某個方法被呼叫之前或之後（或兩者都有），或是某個例外發生的時候。

##### Pointcut

Pointcut是一個定義，藉由這個定義您可以指定某個Aspect在哪些Joinpoint時被應用至應用程式之上。具體的說，您可以在某個定義檔中撰寫Pointcut，當中說明了哪些Aspect要應用至應用程式中的哪些Joinpoint。

##### Target

一個Advice被應用的對象或目標物件，例如 [動態代理](https://openhome.cc/Gossip/SpringGossip/DynamicProxy.html) 中的HelloSpeaker就是LogHandler這個Advice的Target。

##### Introduction

對於一個現存的類別，Introduction可以為其增加行為，而不用修改該類別的程式，具體的說，您可以為某個已撰寫、編譯完成的類別，在執行時期動態加入一些方法或行為，而不用修改或新增任何一行程式碼。

##### Proxy

在《Expert One-on-One J2EE Development WIthout EJB》一書中，Rod Johnson、Juergen Hoeller在第八章中有提到，AOP的實作有五個主要的策略： Dynamic Proxies、Dynamic Byte Code Generation、Java Code Generation、Use of a Custon Class Loader、Language Extensions。

在之前 [從代理機制初探 AOP](https://openhome.cc/Gossip/SpringGossip/FromProxyToAOP.html) 與 [動態代理](https://openhome.cc/Gossip/SpringGossip/DynamicProxy.html) 中，已經使用實際的程式範例介紹過代理機制的實現，Spring的AOP主要是透過動態代理來完成。

##### Weave

Advice被應用至物件之上的過程稱之為縫合（Weave），在AOP中縫合的方式有幾個時間點：編譯時期（Compile time）、類別載入時期（Classload time）、執行時期（Runtime）。


結合 [動態代理](https://openhome.cc/Gossip/SpringGossip/DynamicProxy.html) 的實例，將以上介紹過的AOP相關名詞具體的使用圖片來加以表示，有助於您對每一個名詞的理解與認識：

![img](https://openhome.cc/Gossip/SpringGossip/images/AOPConcept-3.JPG)

----

#### SpringAOP



SpringBootApplication.java

```java
@EnableAspectJAutoProxy
public class SpringBootExampleApplication {
```

logAspect.java

```java
@Component
@Aspect
public class logAspect {
	@Before("execution(* com.example.springBootExample.*.*.*(..))")
	public void before(JoinPoint joinPoint) {	
		Object target = joinPoint.getTarget();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		Logger.getLogger(target.getClass().getName())
		      .info(String.format("aop log :: %s.%s(%s)",
                target.getClass().getName(), methodName, Arrays.toString(args)));
	}
}
```



----

## 第九章. 其他補充

### ResponseBodyEntiy

ResponseEntity繼承於HttpEntity類，HttpEntity類的原始碼的註釋說”HttpEntity主要是和RestTemplate組合起來使用，同樣也可以在SpringMVC中作為@Controller方法的返回值使用”。

ResponseEntity類的原始碼註釋說”ResponseEntity是HttpEntity的擴充套件，增加了一個HttpStutus的狀態碼，通常和RestEntity配合使用，當然也可以在SpringMVC中作為@Controller方法的返回值使用”。

----

### @Query

#### 使用Entity查詢

```java

	@Query(value = "select id, eMail, usrName from Member where eMail = :email and usrName = :usrname ")
	List<Member> selectWhereEmailAndUsrName(@Param("email") String email , @Param("usrname") String usrname);

```



#### 使用原始SQL方式查詢

```java

	@Query(value = "select id, e_mail, usr_name from member where id > :numlast and id < :numfast " ,nativeQuery = true)
	List<Member> selectWhereNumLastlqIdANdNumFastrqId(@Param("numlast") String email , @Param("numfast") String usrname);
```



----

### 資料庫關聯

#### @ManyToOne



#### @OneToMany



#### @ManyToMany





