<!doctype html public "-//W3C//DTD HTML 4.0 Transitional//EN">
        <!-- Group of Computer Architecture / Universität Bremen---------------------------->
        <!-- informatik.uni-bremen.de/agra ------------------------------------------------->
        <!-- created by Lisa Jungmann -------------------------------------------------------->
        <html>
        <head>
<!-- Title ändern -->
        <title>AG RA - Service - Software</title>

        <?php include "inc/meta.inc.php"; ?>
        <link rel="stylesheet" href="../css/agra.css" type="text/css">
        <link rel="stylesheet" href="../css/print.css" type="text/css" media="print">
        <script type="text/javascript" src="js/menu_items.js"></script>
        <script type="text/javascript" src="js/menu.js"></script>
        <script type="text/javascript">
        <!--
        function bookmark()
                {
                my_client = which_browser();
                switch(my_client)
                {
                case 0:
                       var thisSite = document.URL;
                       var thisTitle = document.title;
                       window.external.AddFavorite(thisSite,thisTitle);
                       break;
                case 1:
                       alert("Please type STRG + D on your keyboard");
                       break;
                default:
                       alert("Sorry, your browser does not support this function...");
                       break;
                 }
                 }
        //-->
        </script>
        </head>
        <?php include "inc/header.inc.php"; ?>
<!-- content ändern -->
        <?php include "c.revvis.inc.php"; ?>
          <br><br></td>
          <td width="168" align=left style="background-image:url(images/r_back1a.gif)" class="printno">
            <!-- rechte spalte -->
            <table width="168" border="0" cellspacing="0" cellpadding="0">
            <tr valign=top>
            <td style="background-image:url(images/r_back1.gif)">
            <img src="images/clpix.gif" width="10" height="10" border="0" alt="">

            <?php include "inc/menu_software.inc.php";?>

            <img src="images/clpix.gif" width="10" height="10" border="0" alt="">
            <table width="168" border="0" cellspacing="0" cellpadding="0" background="">
              <tr valign=top>
                <td align=left width="168">
                <a href="javascript:bookmark();" target="_top" title="Add to Favorites"
                   onMouseOver="c25.src='images/drei2a.gif';"
                   onMouseOut="c25.src='images/drei2p.gif';"
                   onFocus=""><img src="images/drei2p.gif" name="c25" width="168" height="16" border="0" alt="Add to Favorites"></a><br>
                </td>
              </tr>
              <tr valign=top>
<!-- die deutsche seite angeben -->
                <td align=left width="168">
                <a href="../ger/revvis.php" target="_top" title="Deutsch"
                   onMouseOver="c26.src='images/drei3a.gif';"
                   onMouseOut="c26.src='images/drei3p.gif';"
                   onFocus=""><img src="images/drei3p.gif" name="c26" width="168" height="16" border="0" alt="Deutsch"></a><br>
                </td>
              </tr>
            </table>
            <br><br>
            <a href="team.php"><img src="images/right_1.gif" width="168" height="49" border="0" alt="Team"></a><br>

            <table width="168" border="0" cellspacing="0" cellpadding="0" background="">
            <tr>
            <td width="9"><img src="images/clpix.gif" width="9" height="10" alt=""><br></td>
            <td width="150">
            <img src="images/clpix.gif" width="10" height="10" alt=""><br>
            <img src="../images/rechts2.jpg" width="150" height="250" border="0" alt=""><br>
            <img src="images/clpix.gif" width="150" height="10" alt=""><br></td>
            <td width="9"><img src="images/clpix.gif" width="9" height="10" alt=""><br></td>
            </tr>
            </table>

            </td></tr>
            <tr><td><img src="images/r_back2.gif" width="168" height="1" border="0" alt=""></td></tr>
            </table>
          </td>
        </tr>
      </table>
      <!-- ende rechte spalte -->
        <?php include "inc/bottom.inc.php"; ?>
        </body>
        </html>
