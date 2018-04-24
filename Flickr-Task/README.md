
# Flickr flipper task

Displays a grid of images from [Flickr's recent photos](https://www.flickr.com/explore).

### App does the following:

* Loads a feed of images from Flickr
* Tap on a specific image to flip it and see its backside
* See image information on the backside. Show image metadata on the backside
* Tap on image again to flip back to original state
* Lazy loading of images (At a time, only 20 images are loaded)
* Link to owner profile
* Error handling on connection failure

### Screenshots

<img src="https://raw.githubusercontent.com/joydeepsaha05/software-engineer-mobile/master/Flickr-Task/screenshots/Screenshot_1524565699.png" width="350">
<img src="https://raw.githubusercontent.com/joydeepsaha05/software-engineer-mobile/master/Flickr-Task/screenshots/Screenshot_1524565720.png" width="350">
<img src="https://raw.githubusercontent.com/joydeepsaha05/software-engineer-mobile/master/Flickr-Task/screenshots/Screenshot_1524565753.png" width="350">

### Compatible with
Android 4.0.3 (API Level 15) - Android 8.1 (API Level 27)

### Download
[Link](https://github.com/joydeepsaha05/software-engineer-mobile/blob/master/Flickr.apk?raw=true)

### Build Information
1. Obtain a Flickr API key by creating an app here: [Link](https://www.flickr.com/services/apps/create/)
2. Create a `gradle.properties` file in your project root
3. Add the following lines to the file you created
```
FLICKR_API_KEY=your-flickr-api-key
FLICKR_API_SECRET=you-flickr-api-secret
```
4. Run the app
