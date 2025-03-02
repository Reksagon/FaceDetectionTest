### **Face Detection & Recognition App**
📸 **FaceDetectionTest** – це додаток для розпізнавання облич, який використовує **ML Kit Face Detection API** для ідентифікації людей на фото та порівняння їх із базою даних.

---

## 🚀 **Інструкція для запуску**
### **1. Вимоги**
Перед запуском проєкту переконайтеся, що у вас встановлено:
- **Android Studio Giraffe (або новішої версії)**
- **Gradle 8+**
- **Java 11+**
- **Android SDK 26+ (мінімальна версія), Target SDK 35**
- **Пристрій або емулятор з підтримкою камери (API 26+)**

---

### **2. Клонування репозиторію**
```sh
git clone https://github.com/Reksagon/FaceDetectionTest.git
cd FaceDetectionTest
```

---

### **3. Встановлення залежностей**
Gradle автоматично завантажить усі необхідні бібліотеки. Якщо виникли проблеми, запустіть:
```sh
./gradlew build
```

---

### **4. Запуск на емуляторі або пристрої**
#### 🔹 **Запуск у Android Studio**  
1. Відкрийте проєкт у **Android Studio**  
2. Запустіть **емулятор або підключіть реальний пристрій**  
3. Натисніть ▶ **Run (Shift + F10)**  

#### 🔹 **Запуск через командний рядок**
```sh
./gradlew installDebug
adb shell am start -n com.korniienko.facedetectiontest/.MainActivity
```

---

## 🏗 **Архітектура проєкту**
- **MVVM (Model-View-ViewModel)** – архітектурний патерн  
- **ML Kit Face Detection API** – для розпізнавання облич  
- **Room Database** – для збереження осіб  
- **Dagger Hilt** – для DI  
- **Coroutines** – для асинхронної роботи  

---

## 🤖 **Як працює алгоритм порівняння облич**
### **1. Захоплення зображення**
Користувач може:
- 📷 **Зробити фото** (через камеру)  
- 📁 **Обрати зображення** (з галереї)  

Зображення передається у `FaceDetectionHelper`, де створюється **InputImage**.

---

### **2. Виявлення облич**
За допомогою **ML Kit Face Detection API** визначаються обличчя:
```kotlin
suspend fun detectFaces(bitmap: Bitmap): List<Rect> = withContext(Dispatchers.IO) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val faces = Tasks.await(detector.process(image))
    faces.map { it.boundingBox }
}
```
Якщо обличчя не знайдено → виводиться повідомлення **"Обличчя не розпізнано"**.

---

### **3. Порівняння із збереженими обличчями**
Для кожної особи з бази даних отримується її збережене фото та шукається збіг **Bounding Box (Rect)**:
```kotlin
if (storedFaces.any { storedFace -> detectedFaces.any { it.intersect(storedFace) } }) {
    matchedPersons.add(person)
}
```
Якщо знайдено збіг, повертається список розпізнаних осіб.

---

## 🧪 **Тестування**
### **Юніт-тести**
Проєкт містить тести для:
- **FaceDetectionHelperTest** – Перевірка розпізнавання облич
- **RecognizeFaceUseCaseTest** – Перевірка логіки порівняння облич
- **PersonRepositoryImplTest** – Тестує збереження та отримання осіб із бази
- **AddPersonUseCaseTest** – Перевіряє, чи можна додати особу в базу
- **MainCoroutineRule** – Налаштовує корутини для тестування 

Запустити тести можна командою:
```sh
./gradlew test
```

---

## ⚠ **Необхідні дозволи**
Перед запуском додаток запитує такі дозволи:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```
Якщо доступ до камери або галереї не надано, додаток відобразить запит дозволу.

---

## 📌 **Можливі помилки та їх виправлення**
| Помилка | Вирішення |
|---------|-----------|
| `java.lang.SecurityException: Permission Denial` | Переконайтеся, що додано **CAMERA** та **READ_STORAGE** |
| `android.graphics.Rect not mocked` | Додайте `mockk(relaxed = true)` у юніт-тестах |
| `java.lang.AbstractMethodError: getContentResolver()` | Використовуйте `mockk<Context>(relaxed = true)` у тестах |

---

## ✨ **Автор**
👨‍💻 **Denys Korniienko**  
📧 Email: den.kornienko2012@gmail.com
🔗 [GitHub](https://github.com/Reksagon)  
