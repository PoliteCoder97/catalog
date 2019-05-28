package project.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import project.classes.Consts;
import project.classes.Slide;

public class Utils {

    public static String checkVersionAndBuildUrl(String uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Consts.HTTPS_REQUEST + uri;
        }
        return Consts.HTTP_REQUEST + uri;
    }

    public static List<Slide> extractSlides(String data) {
        List<Slide> slides = new ArrayList<>();
        try {
            JSONArray jaSlides = new JSONArray(data);
            for (int j = 0; j < jaSlides.length(); j++) {
                JSONObject joSlide = jaSlides.getJSONObject(j);
                Slide slide = new Slide();
                try {
                    slide.setImageName(joSlide.getString("image"));
                } catch (Exception ignored) {
                }

                try {
                    slide.setAction(joSlide.getString("action"));
                } catch (Exception ignored) {
                }
                try {
                    slide.setActionData(joSlide.getString("actionData"));
                } catch (Exception ignored) {
                }
                try {
                    slide.setId(joSlide.getLong("id"));
                } catch (Exception ignored) {
                }
                slides.add(slide);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slides;
    }


//
//  View.OnClickListener oclContact = new View.OnClickListener() {
//    @Override
//    public void onClick(final View v) {
//      int index = (int) v.getTag();
//      Contact contact = mContacts.get(index);
//      switch (contact.getType()) {
//        case EMAIL:
//          try {
//            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//            emailIntent.setType("plain/text");
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.getContent()});
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ایمیل از طرف اپلیکیشن");
//            emailIntent.putExtra(Intent.EXTRA_TEXT, " ");
//            ContactActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//          } catch (Exception ignored) {
//          }
//
//          break;
//        case ADDRESS:
//
//          break;
//        case WEB:
//
//          try {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contact.getContent()));
//            ContactActivity.this.startActivity(intent);
//          } catch (Exception ignored) {
//          }
//
//          break;
//        case WHATSAPP:
//
//          try {
//            String url = "https://api.whatsapp.com/send?phone=" + contact.getContent();
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            ContactActivity.this.startActivity(i);
//          } catch (Exception e) {
//            Dialogs.showToast(ContactActivity.this, "واتساپ نصب نیست!", Toast.LENGTH_SHORT, Dialogs.ToastType.WARNING);
//          }
//
//          break;
//        case TWEETER:
//
//          try {
//            ContactActivity.this
//              .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + contact.getContent())));
//          } catch (Exception e) {
//            ContactActivity.this
//              .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + contact.getContent())));
//          }
//
//          break;
//        case TELEGRAM:
//
//          try {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + contact.getContent()));
//            ContactActivity.this.startActivity(intent);
//          } catch (Exception e) {
//            e.printStackTrace();
//            Dialogs.showToast(ContactActivity.this, "تلگرام نصب نیست!", Toast.LENGTH_SHORT, Dialogs.ToastType.WARNING);
//          }
//
//          break;
//        case INSTAGRAM:
//
//          try {
//            Intent likeIng = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + contact.getContent()));
//            likeIng.setPackage("com.instagram.android");
//            ContactActivity.this.startActivity(likeIng);
//          } catch (ActivityNotFoundException e) {
//            ContactActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
//              Uri.parse("http://instagram.com/" + contact.getContent())));
//          }
//
//          break;
//        case PHONE:
//
//          try {
//            Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + contact.getContent()));
//            ContactActivity.this.startActivity(intent);
//          } catch (Exception ignored) {
//          }
//
//          break;
//        case SMS:
//
//          try {
//            Uri uri = Uri.parse("smsto:" + contact.getContent());
//            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//            intent.putExtra("sms_body", "");
//            ContactActivity.this.startActivity(intent);
//          } catch (Exception ignored) {
//            ignored.printStackTrace();
//          }
//
//          break;
//        case MOBILE:
//          try {
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getContent()));
//            ContactActivity.this.startActivity(intent);
//          } catch (Exception ignored) {
//            ignored.printStackTrace();
//          }
//
//          break;
//        case MAP:
//          try {
//            final String[] arContent = contact.getContent().split("\\|");
//            try {
//              String label = "ABC Label";
//              String uriBegin = "geo:" + arContent[1] + "," + arContent[2];
//              String query = arContent[1] + "," + arContent[2] + "(" + label + ")";
//              String encodedQuery = Uri.encode(query);
//              String uriString = uriBegin + "?q=" + encodedQuery + "&z=" + arContent[3];
//              Uri uri = Uri.parse(uriString);
//              Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//              startActivity(intent);
//            } catch (Exception ignored) {
//              try {
//
//                String uri = "http://maps.google.com/maps?q=loc:" + arContent[1] + "," + arContent[2] + " (" + "map" + ")";
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                ContactActivity.this.startActivity(intent);
//              } catch (Exception ignored2) {
//              }
//            }
//          } catch (Exception ignored2) {
//          }
//          break;
//        case TITLE:
//
//          break;
//        default:
//      }
//    }
//  };

    public static void call(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static void openTelegram(Context context, String telegram) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + telegram));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "No app such as telegram", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openWhatsapp(Context context, String whatsapp) {
        try {
            String url = "https://api.whatsapp.com/send?phone=" + whatsapp;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(context, "No app such as whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openEmail(Context context, String email) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email From Application");
            emailIntent.putExtra(Intent.EXTRA_TEXT, " ");
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (Exception ignored) {
        }
    }

    public static void openWeb(Context context, String web) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(web));
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }


}
