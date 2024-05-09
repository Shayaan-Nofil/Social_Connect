import java.io.Serializable

class User: Serializable {
    var id: String = ""
    var name:String = ""
    var email: String = ""
    var number: String = ""
    var city: String = ""
    var  country: String = ""
    var token: String = ""
    var profilepic: String = "https://firebasestorage.googleapis.com/v0/b/social-connect-92415.appspot.com/o/profilepics%2Fali_profilepic.png?alt=media&token=bb9f99fd-f802-46f0-870a-afbe0aa9df6c"
    var bgpic: String = "https://firebasestorage.googleapis.com/v0/b/social-connect-92415.appspot.com/o/backgroundpics%2Fali_backdrop.jpg?alt=media&token=8a96d9d7-139f-4d95-b696-2792f5827301"
    fun addData (ID:String = "", nam:String = "", eml: String = "" , num: String = "", cty: String = "" , ctr: String){
        name = nam
        email = eml
        number = num
        city = cty
        country = ctr
    }
}