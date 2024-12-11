import base64
import io
from typing import List
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from models.ContractorPerformance import ContractorPerformance


def process_data_and_generate_graph(contractor_performance_list: List[ContractorPerformance]) -> dict:
    hearingDfList = []
    warningDfList = []
    attendanceDfList = []
    aptidtudeTestDfList = []
    contractorsDfList = []

    dfHeadersContractors = ["Name", "Surname", "Age", "Race", "Gender"]

    dfHeadersHearings = ["Name", "Surname", "Age", "Race", "Gender", "Location", "Status", "Start Date",
                         "End Date", "Hearing Schedule Date", "Hearing Reason", "Hearing Outcome"]

    dfHeadersWarnings = ["Name", "Surname", "Age", "Race", "Gender", "Location", "Status", "Start Date",
                         "End Date", "Warning Reason", "Warning State", "Warning Date Issued"]

    dfHeadersAttendance = ["Name", "Surname", "Age", "Race", "Gender", "Location", "Status", "Start Date",
                           "End Date", "Time In", "Time Out", "Attendance Register"]

    dfHeadersAptitudeTest = ["Name", "Surname", "Age", "Race", "Gender", "Location", "Status", "Start Date",
                             "End Date", "Aptitude Test Date", "Aptitude Test Mark"]

    for contractor_performance in contractor_performance_list:
        # Extract attributes using dictionary access
        user = contractor_performance.user
        print(user)
        contract_period = contractor_performance.contractPeriod
        contractor = contractor_performance.contractor
        aptitude_test = contractor_performance.aptitudeTest

        # Process hearing list
        for hearing in contractor_performance.hearingList:
            hearingList = [
                user['name'], user['surname'], user['age'], user['race'], user['gender'],
                user['location'], contractor['status'],
                contract_period['startDate'], contract_period['endDate'],
                hearing['scheduleDate'], hearing['reason'], hearing['outcome']
            ]

            hearingDfList.append(hearingList)

        # Process warning list
        for warning in contractor_performance.warningList:
            warningList = [
                user['name'], user['surname'], user['age'], user['race'], user['gender'],
                user['location'], contractor['status'],
                contract_period['startDate'], contract_period['endDate'],
                warning['reason'], warning.get("state"), warning['dateIssue']
            ]
            warningDfList.append(warningList)

        # Process attendance list
        for attendance in contractor_performance.attendanceList:
            attendanceList = [
                user['name'], user['surname'], user['age'], user['race'], user['gender'],
                user['location'], contractor['status'],
                contract_period['startDate'], contract_period['endDate'],
                attendance['timeIn'], attendance['timeOut'], attendance['register']
            ]
            attendanceDfList.append(attendanceList)

        # Process aptitude test
        if contractor_performance.aptitudeTest and contractor_performance.aptitudeTest.get('testMark') is not None:
            aptitudeTestList = [
                user['name'], user['surname'], user['age'], user['race'], user['gender'],
                user['location'], contractor['status'],
                contract_period['startDate'], contract_period['endDate'],
                contractor_performance.aptitudeTest['testDate'], contractor_performance.aptitudeTest['testMark']
            ]
            aptidtudeTestDfList.append(aptitudeTestList)

        contractorsList = [
            user['name'], user['surname'], user['age'], user['race'], user['gender']]
        contractorsDfList.append(contractorsList)

    # Create DataFrames
    dfHearings = pd.DataFrame(hearingDfList, columns=dfHeadersHearings)
    dfWarnings = pd.DataFrame(warningDfList, columns=dfHeadersWarnings)
    dfAttendance = pd.DataFrame(attendanceDfList, columns=dfHeadersAttendance)
    dfAptitudeTest = pd.DataFrame(aptidtudeTestDfList, columns=dfHeadersAptitudeTest)
    dfContractors = pd.DataFrame(data=contractorsDfList, columns=dfHeadersContractors)

    print(dfHearings.head())
    print(dfWarnings.head())
    print(dfAttendance.head())
    print(dfAptitudeTest.head())
    print(dfContractors.head())

    if not dfContractors.empty:

        graphs = {}
        try:
            # Gender Pie Chart
            fig1, ax1 = plt.subplots(figsize=(7, 7))
            gender_pie = dfContractors["Gender"].value_counts()
            gender_pie.plot(kind="pie", autopct="%1.1f%%", ax=ax1, cmap="Set2", legend=True)
            plt.title("Gender Distribution Within the Company")
            plt.tight_layout()
            buf1 = io.BytesIO()
            plt.savefig(buf1, format="png")
            plt.close(fig1)
            buf1.seek(0)
            graphs["Pie Chart Gender Distribution"] = base64.b64encode(buf1.read()).decode("utf-8")
            buf1.close()

            # Race Pie Chart
            fig2, ax2 = plt.subplots(figsize=(7, 7))
            race_pie = dfContractors["Race"].value_counts()
            race_pie.plot(kind="pie", autopct="%1.1f%%", ax=ax2, cmap="Set2", legend=True)
            plt.title("Race Distribution Within the Company")
            plt.tight_layout()
            buf2 = io.BytesIO()
            plt.savefig(buf2, format="png")
            plt.close(fig2)
            buf2.seek(0)
            graphs["Pie Chart Race Distribution"] = base64.b64encode(buf2.read()).decode("utf-8")
            buf2.close()

            # Aptitude Test Graphs
            if not dfAptitudeTest.empty:
                # Boxplot for aptitude test by Gender
                fig3, ax3 = plt.subplots(figsize=(6, 4))
                sns.boxplot(x='Gender', data=dfAptitudeTest, y='Aptitude Test Mark', ax=ax3, palette="muted")
                plt.title("Aptitude Test Scores")
                plt.tight_layout()
                buf3 = io.BytesIO()
                plt.savefig(buf3, format='png')
                plt.close(fig3)
                buf3.seek(0)
                graphs["Boxplot Aptitude Test by Gender"] = base64.b64encode(buf3.read()).decode('utf-8')
                buf3.close()

                # Histogram for Aptitude Test Distribution
                fig4, ax4 = plt.subplots(figsize=(6, 4))
                sns.histplot(dfAptitudeTest["Aptitude Test Mark"], bins=6, kde=False, ax=ax4, color='darkblue')
                plt.title("Distribution of Aptitude Test Marks")
                plt.xlabel("Aptitude Test Marks")
                plt.ylabel("Counts")
                plt.xticks(rotation=90)
                plt.tight_layout()
                buf4 = io.BytesIO()
                plt.savefig(buf4, format="png")
                plt.close(fig4)
                buf4.seek(0)
                graphs["Histogram Aptitude Test Counts"] = base64.b64encode(buf4.read()).decode('utf-8')
                buf4.close()

                # Scatter plot showing Age vs Test Score
                fig5, ax5 = plt.subplots(figsize=(5, 4))
                sns.scatterplot(data=dfAptitudeTest, x="Age", y="Aptitude Test Mark", ax=ax5, color='darkgreen')
                plt.title("Age vs Test Score Plot")
                plt.xlabel("Age")
                plt.ylabel("Test Score")
                plt.tight_layout()
                buf5 = io.BytesIO()
                plt.savefig(buf5, format="png")
                plt.close(fig5)
                buf5.seek(0)
                graphs["Scatter Plot Age vs Test Score"] = base64.b64encode(buf5.read()).decode("utf-8")
                buf5.close()

                # Bar graph Mean Test Scores by Location
                fig6, ax6 = plt.subplots(figsize=(6, 4))
                location_mean = dfAptitudeTest.groupby('Location')['Aptitude Test Mark'].mean().reset_index()
                location_mean = location_mean.sort_values(by='Aptitude Test Mark', ascending=False)
                sns.barplot(data=location_mean, x="Location", y="Aptitude Test Mark", palette="deep", ax=ax6)
                plt.title("Mean Test Scores by Location")
                plt.xlabel("Location")
                plt.ylabel("Mean")
                plt.xticks(rotation=90)  # Make sure labels are readable
                plt.tight_layout()
                buf6 = io.BytesIO()
                plt.savefig(buf6, format="png")
                plt.close(fig6)
                buf6.seek(0)
                graphs["Bargraph Mean Test Scores Per Area"] = base64.b64encode(buf6.read()).decode("utf-8")
                buf6.close()

            # Warnings Graphs
            if not dfWarnings.empty:
                # Bargraph of Warnings by Reason
                fig7, ax7 = plt.subplots(figsize=(6, 4))
                reasons_count = dfWarnings.groupby('Warning Reason').size().reset_index(name='Count')
                reasons_count = reasons_count.sort_values(by='Count', ascending=False)
                sns.barplot(data=reasons_count, x="Warning Reason", y="Count", palette="deep", ax=ax7)
                plt.title("Number of Warnings by Reason")
                plt.xlabel("Reason")
                plt.ylabel("Count")
                plt.xticks(rotation=90)  # Ensure x labels are always visible
                plt.tight_layout()
                buf7 = io.BytesIO()
                plt.savefig(buf7, format="png")
                plt.close(fig7)
                buf7.seek(0)
                graphs["Bargraph Warnings by Reason"] = base64.b64encode(buf7.read()).decode("utf-8")
                buf7.close()

                # Stacked Bar Chart of Warnings by Location and Reason
                fig8, ax8 = plt.subplots(figsize=(6, 4))
                pivot_data = dfWarnings.pivot_table(index='Location', columns='Warning Reason', aggfunc='size',
                                                    fill_value=0)
                pivot_data_sorted = pivot_data.sort_index(axis=0, ascending=True)
                pivot_data_sorted.plot(kind='bar', stacked=True, ax=ax8, cmap='Set3')
                plt.title("Stacked Bar Chart of Warnings by Location and Reason")
                plt.xlabel("Location")
                plt.ylabel("Number of Warnings")
                plt.tight_layout()
                plt.xticks(rotation=90)  # Ensure x labels are always visible
                plt.tight_layout()  # Auto adjust layout
                buf8 = io.BytesIO()
                plt.savefig(buf8, format="png")
                plt.close(fig8)
                buf8.seek(0)
                graphs["Stacked Bar Chart Warnings by Location"] = base64.b64encode(buf8.read()).decode("utf-8")
                buf8.close()

                # Assuming 'dfWarnings' already has a 'Full Name' column created like this:
                dfWarnings['Full Name'] = dfWarnings['Name'] + " " + dfWarnings['Surname']  # Combine Name and Surname

                # Pivot data by 'Full Name' and 'Warning Reason'
                pivot_data_user = dfWarnings.pivot_table(index='Full Name', columns='Warning Reason', aggfunc='size',
                                                         fill_value=0)

                # Sort the pivoted data
                pivot_data_user_sorted = pivot_data_user.sort_index(axis=0, ascending=True)

                # Plot the Stacked Bar Chart of Warnings by User and Reason
                fig9, ax9 = plt.subplots(figsize=(6, 4))  # Adjust figure size as needed
                pivot_data_user_sorted.plot(kind='bar', stacked=True, ax=ax9, cmap='Set3')

                # Add title and labels
                plt.title("Stacked Bar Chart of Warnings by User and Reason")
                plt.xlabel("User")
                plt.ylabel("Number of Warnings")
                plt.xticks(rotation=90)  # Rotate x-axis labels to make them visible
                plt.tight_layout()  # Adjust layout to prevent clipping

                # Save the plot to a BytesIO object and convert to base64
                buf9 = io.BytesIO()
                plt.savefig(buf9, format="png")
                plt.close(fig9)

                # Move the cursor to the beginning of the buffer and encode the image to base64
                buf9.seek(0)
                graphs["Stacked Bar Chart Warnings by User"] = base64.b64encode(buf9.read()).decode("utf-8")

                # Close the buffer after use
                buf9.close()

                # Boxplot for Age Distribution of Warnings
                fig10, ax10 = plt.subplots(figsize=(4, 4))
                sns.boxplot(data=dfWarnings, x="Warning Reason", y="Age", ax=ax10, palette="Set2")
                plt.title("Age Distribution of Warnings")
                plt.tight_layout()
                buf10 = io.BytesIO()
                plt.savefig(buf10, format="png")
                plt.close(fig10)
                buf10.seek(0)
                graphs["Boxplot Age Distribution of Warnings"] = base64.b64encode(buf10.read()).decode("utf-8")
                buf10.close()

            # Hearing Graphs
            if not dfHearings.empty:
                # Bargraph of Hearings by Outcome
                fig11, ax11 = plt.subplots(figsize=(6, 4))
                reasons_count_hearings = dfHearings.groupby('Hearing Outcome').size().reset_index(name='Count')
                reasons_count_hearings = reasons_count_hearings.sort_values(by='Count', ascending=False)
                sns.barplot(data=reasons_count_hearings, x="Hearing Outcome", y="Count", palette="deep", ax=ax11)
                plt.title("Number of Hearings grouped by Outcome")
                plt.xlabel("Outcome")
                plt.ylabel("Count")
                plt.xticks(rotation=90)  # Ensure x labels are always visible
                plt.tight_layout()
                buf11 = io.BytesIO()
                plt.savefig(buf11, format="png")
                plt.close(fig11)
                buf11.seek(0)
                graphs["Bargraph Hearings by Outcome"] = base64.b64encode(buf11.read()).decode("utf-8")
                buf11.close()

            return graphs



        except Exception as e:
            # Handle errors (e.g., log them or return an error message)
            print(e.with_traceback())
            print(f"Error generating graph: {e}")
            graph_base64 = None  # Or a placeholder image string

        return graph_base64
    else:
        return {}
